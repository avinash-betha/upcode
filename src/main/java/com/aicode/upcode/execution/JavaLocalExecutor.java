package com.aicode.upcode.execution;

import com.aicode.upcode.domain.Language;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

@Component
public class JavaLocalExecutor implements CodeExecutor {

    @Override
    public boolean supports(Language language) {
        return language == Language.JAVA;
    }

    @Override
    public ExecutionResult execute(Language language,
                                   String code,
                                   String input,
                                   long timeLimitMs) {

        Path tempDir = null;

        try {
            long startTime = System.currentTimeMillis();

            // 🧠 Memory before execution
            long beforeMemory = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();

            // 1️⃣ Create temp directory
            tempDir = Files.createTempDirectory("upcode_");

            // 2️⃣ Write Main.java
            Path sourceFile = tempDir.resolve("Main.java");
            Files.writeString(sourceFile, code);

            // 3️⃣ Compile
            Process compileProcess = new ProcessBuilder("javac", "Main.java")
                    .directory(tempDir.toFile())
                    .redirectErrorStream(true)
                    .start();

            String compileOutput = readProcessOutput(compileProcess);

            int compileExit = compileProcess.waitFor();

            if (compileExit != 0) {
                cleanup(tempDir);
                return new ExecutionResult(
                        false,
                        "Compilation Error:\n" + compileOutput,
                        0,
                        0
                );
            }

            // 4️⃣ Run program
            Process runProcess = new ProcessBuilder("java", "Main")
                    .directory(tempDir.toFile())
                    .redirectErrorStream(true)
                    .start();

            // 5️⃣ Send input if exists
            if (input != null && !input.isBlank()) {
                try (BufferedWriter writer =
                             new BufferedWriter(new OutputStreamWriter(runProcess.getOutputStream()))) {
                    writer.write(input);
                    writer.flush();
                }
            }

            // 6️⃣ Wait with timeout
            boolean finished = runProcess.waitFor(timeLimitMs, TimeUnit.MILLISECONDS);

            if (!finished) {
                runProcess.destroyForcibly();
                cleanup(tempDir);
                return new ExecutionResult(
                        false,
                        "Time Limit Exceeded",
                        timeLimitMs,
                        0
                );
            }

            String runOutput = readProcessOutput(runProcess);

            int runExit = runProcess.exitValue();

            long endTime = System.currentTimeMillis();

            // 🧠 Memory after execution
            long afterMemory = Runtime.getRuntime().totalMemory()
                    - Runtime.getRuntime().freeMemory();

            long memoryUsedKb = Math.max(0, (afterMemory - beforeMemory) / 1024);

            cleanup(tempDir);

            if (runExit != 0) {
                return new ExecutionResult(
                        false,
                        "Runtime Error:\n" + runOutput,
                        endTime - startTime,
                        memoryUsedKb
                );
            }

            return new ExecutionResult(
                    true,
                    runOutput.trim(),
                    endTime - startTime,
                    memoryUsedKb
            );

        } catch (Exception e) {
            if (tempDir != null) {
                cleanup(tempDir);
            }

            return new ExecutionResult(
                    false,
                    "Execution Failed: " + e.getMessage(),
                    0,
                    0
            );
        }
    }

    // -----------------------------------
    // Utility: Read process output safely
    // -----------------------------------
    private String readProcessOutput(Process process) throws IOException {

        StringBuilder output = new StringBuilder();

        try (BufferedReader reader =
                     new BufferedReader(new InputStreamReader(process.getInputStream()))) {

            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
        }

        return output.toString();
    }

    // -----------------------------------
    // Utility: Cleanup temp directory
    // -----------------------------------
    private void cleanup(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted((a, b) -> b.compareTo(a)) // delete children first
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }
}
