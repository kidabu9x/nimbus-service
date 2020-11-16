package vn.com.nimbus.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;
import vn.com.nimbus.common.exception.AppException;
import vn.com.nimbus.common.exception.AppExceptionCode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FileUtils {
    public static File convert(MultipartFile file) throws IOException {
        File convFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static void delete(File file) {
        file.delete();
    }

    public static void deleteAll(List<File> files) {
        files.forEach(File::delete);
    }

    public static Optional<String> getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public static File saveFile(FilePart filePart) {
        log.info("handling file upload {}", filePart.filename());

        // if a file with the same name already exists in a repository, delete and recreate it
        final String filename = filePart.filename();
        File file = new File(filename);
        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            log.error("Error on creating file, ex: {}", e.getMessage());
            throw new AppException(AppExceptionCode.FAIL_TO_CONVERT_FILE);
        }
        return file; // if creating a new file fails return an error
//
//        try {
//            // create an async file channel to store the file on disk
//            final AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(file.toPath(), StandardOpenOption.WRITE);
//
//            final FileUtils.CloseCondition closeCondition = new FileUtils.CloseCondition();
//
//            // pointer to the end of file offset
//            AtomicInteger fileWriteOffset = new AtomicInteger(0);
//            // error signal
//            AtomicBoolean errorFlag = new AtomicBoolean(false);
//
//            log.info("subscribing to file parts");
//            // FilePart.content produces a flux of data buffers, each need to be written to the file
//            return filePart.content().doOnEach(dataBufferSignal -> {
//                if (dataBufferSignal.hasValue() && !errorFlag.get()) {
//                    // read data from the incoming data buffer into a file array
//                    DataBuffer dataBuffer = dataBufferSignal.get();
//                    int count = dataBuffer.readableByteCount();
//                    byte[] bytes = new byte[count];
//                    dataBuffer.read(bytes);
//
//                    // create a file channel compatible byte buffer
//                    final ByteBuffer byteBuffer = ByteBuffer.allocate(count);
//                    byteBuffer.put(bytes);
//                    byteBuffer.flip();
//
//                    // get the current write offset and increment by the buffer size
//                    final int filePartOffset = fileWriteOffset.getAndAdd(count);
//                    log.info("processing file part at offset {}", filePartOffset);
//                    // write the buffer to disk
//                    closeCondition.onTaskSubmitted();
//                    fileChannel.write(byteBuffer, filePartOffset, null, new CompletionHandler<Integer, ByteBuffer>() {
//                        @Override
//                        public void completed(Integer result, ByteBuffer attachment) {
//                            // file part successfuly written to disk, clean up
//                            log.info("done saving file part {}", filePartOffset);
//                            byteBuffer.clear();
//
//                            if (closeCondition.onTaskCompleted())
//                                try {
//                                    log.info("closing after last part");
//                                    fileChannel.close();
//                                } catch (IOException ignored) {
//                                    ignored.printStackTrace();
//                                }
//                        }
//
//                        @Override
//                        public void failed(Throwable exc, ByteBuffer attachment) {
//                            // there as an error while writing to disk, set an error flag
//                            errorFlag.set(true);
//                            log.info("error saving file part {}", filePartOffset);
//                        }
//                    });
//                }
//            }).doOnComplete(() -> {
//                // all done, close the file channel
//                log.info("done processing file parts");
//                if (closeCondition.canCloseOnComplete())
//                    try {
//                        log.info("closing after complete");
//                        fileChannel.close();
//                    } catch (IOException ignored) {
//                    }
//
//            }).doOnError(t -> {
//                // ooops there was an error
//                log.info("error processing file parts");
//                try {
//                    fileChannel.close();
//                } catch (IOException ignored) {
//                }
//                // take last, map to a status string
//            }).last().map(dataBuffer -> filePart.filename() + " " + (errorFlag.get() ? "error" : "uploaded"));
//        } catch (IOException e) {
//            // unable to open the file channel, return an error
//            log.info("error opening the file channel");
//            throw new AppException(AppExceptionCode.FAIL_TO_CONVERT_FILE);
//        }
    }


    public static class CloseCondition {

        AtomicInteger tasksSubmitted = new AtomicInteger(0);
        AtomicInteger tasksCompleted = new AtomicInteger(0);
        AtomicBoolean allTaskssubmitted = new AtomicBoolean(false);

        /**
         * notify all tasks have been subitted, determine of the file channel can be closed
         * @return true if the asynchronous file stream can be closed
         */
        public boolean canCloseOnComplete() {
            allTaskssubmitted.set(true);
            return tasksCompleted.get() == tasksSubmitted.get();
        }

        /**
         * notify a task has been submitted
         */
        public void onTaskSubmitted() {
            tasksSubmitted.incrementAndGet();
        }

        /**
         * notify a task has been completed
         * @return true if the asynchronous file stream can be closed
         */
        public boolean onTaskCompleted() {
            boolean allSubmittedClosed = tasksSubmitted.get() == tasksCompleted.incrementAndGet();
            return allSubmittedClosed && allTaskssubmitted.get();
        }
    }

}
