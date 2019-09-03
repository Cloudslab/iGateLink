package org.cloudbus.foggatewaylib.aneka;

import com.manjrasoft.aneka.ApplicationResult;
import com.manjrasoft.aneka.ArrayOfFile;
import com.manjrasoft.aneka.ArrayOfTaskItem;
import com.manjrasoft.aneka.CopyTaskItem;
import com.manjrasoft.aneka.ExecuteTaskItem;
import com.manjrasoft.aneka.Job;
import com.manjrasoft.aneka.TaskItem;

import org.cloudbus.foggatewaylib.aneka.ftp.SimpleFTPClient;
import org.junit.Test;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

import static com.manjrasoft.aneka.JobStatus.STATUS_COMPLETED;
import static org.junit.Assert.assertEquals;

public class AnekaWebServicesTest {
    final static String APP_NAME = "My Application";

    @Test
    public void test1() {
        boolean check;

        AnekaWebServices services = new AnekaWebServices(Credentials.URL, true);
        check = services.authenticateUser(Credentials.USERNAME, Credentials.PASSWORD);

        assert check;

        String applicationId = services.createApplicationWait(APP_NAME);

        assert applicationId != null;

        try{
            ApplicationResult result = services.queryApplication(applicationId);

            assert result.getCreatedDateTime().before(new Date());
            assert result.getDisplayName().equals(APP_NAME);
            assert result.isUseFileTransfer();
            assert result.getJobs().getJobResult().length == 0;
            assert result.getFinishedDateTime() == null;

            ExecuteTaskItem task = new ExecuteTaskItem();
            task.setCommand("ping");
            task.setArguments("localhost -n 5");
            ArrayOfTaskItem tasks = new ArrayOfTaskItem();
            tasks.setTaskItem(new TaskItem[]{task});

            Job job = new Job();
            job.setTasks(tasks);

            String jobId = services.submitJobWait(applicationId, job);

            assert jobId != null;


            String termination_status = services.waitJobTermination(applicationId, jobId);

            assert termination_status.equals(STATUS_COMPLETED);
        } finally {
            if (services.getError() != null){
                System.out.println(services.getError());
            }
            boolean deleted = services.abortApplication(applicationId);
            assert  deleted;
        }
    }

    @Test
    public void test2() {
        AnekaWebServices services = new AnekaWebServices(Credentials.URL, true);
        String myid = UUID.randomUUID().toString();

        System.out.println(myid);

        try {
            boolean check;
            check = services.authenticateUser(Credentials.USERNAME, Credentials.PASSWORD);

            assert check;

            StorageBucket bucketIn = new FTPStorageBucket(
                    "InputFTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.HOST);

            StorageBucket bucketOut = new FTPStorageBucket(
                    "OutputFTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.HOST);

            String applicationId = services.createApplicationWait(APP_NAME, bucketIn, bucketOut);

            assert applicationId != null;

            try {
                ApplicationResult result = services.queryApplication(applicationId);

                assert result.getCreatedDateTime().before(new Date());
                assert result.getDisplayName().equals(APP_NAME);
                assert result.isUseFileTransfer();
                assert result.getJobs().getJobResult().length == 0;
                assert result.getFinishedDateTime() == null;

                ArrayOfFile inputFiles = WSDLBuilder.buildArrayOfFile(
                        "InputFTP",
                        "/input.jpg",
                        "input.jpg");

                ArrayOfFile outputFiles = WSDLBuilder.buildArrayOfFile(
                        "OutputFTP",
                        "/" + myid + "/output.jpg",
                        "output.jpg");

                CopyTaskItem copyTask = new CopyTaskItem();
                copyTask.setSource("input.jpg");
                copyTask.setTarget("output.jpg");

//            ExecuteTaskItem sleepTask = new ExecuteTaskItem();
//            sleepTask.setCommand("ping");
//            sleepTask.setArguments("localhost -n 15");

                ArrayOfTaskItem tasks = new ArrayOfTaskItem();
                tasks.setTaskItem(new TaskItem[]{copyTask});

                Job job = new Job();
                job.setInputFiles(inputFiles);
                job.setOutputFiles(outputFiles);
                job.setTasks(tasks);

                String jobId = services.submitJobWait(applicationId, job);

                assert jobId != null;

                String termination_status = services.waitJobTermination(applicationId, jobId);

                assertEquals(STATUS_COMPLETED, termination_status);
            } finally {
                boolean deleted = services.abortApplication(applicationId);
                assert deleted;
            }
        } finally {
            if (services.getError() != null) {
                System.out.println(services.getError());
            }
        }
    }

    @Test
    public void test3() {
        AnekaWebServices services = new AnekaWebServices(Credentials.URL, true);
        String myid = UUID.randomUUID().toString();

        System.out.println(myid);

        try {
            boolean check;
            check = services.authenticateUser(Credentials.USERNAME, Credentials.PASSWORD);

            assert check;

            StorageBucket bucket = new FTPStorageBucket(
                    "FTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.HOST);

            ArrayOfFile sharedFiles = WSDLBuilder.buildArrayOfFile(
                    "FTP",
                    "",
                    "",
                    new String[]{"Yolo.zip"}
            );

            String applicationId = services.createApplicationWait(APP_NAME, sharedFiles, bucket);

            assert applicationId != null;

            try {
                ApplicationResult result = services.queryApplication(applicationId);

                assert result.getCreatedDateTime().before(new Date());
                assert result.getDisplayName().equals(APP_NAME);
                assert result.isUseFileTransfer();
                assert result.getJobs().getJobResult().length == 0;
                assert result.getFinishedDateTime() == null;

                ArrayOfFile inputFiles = WSDLBuilder.buildArrayOfFile(
                        "FTP",
                        "/input.jpg",
                        "input/input.jpg");

                ArrayOfFile outputFiles = WSDLBuilder.buildArrayOfFile(
                        "FTP",
                        "/" + myid + "/output.jpg",
                        "output/0_0.jpg");

                ExecuteTaskItem unzipTask = WSDLBuilder.buildExecuteTaskItem(
                        "7z",
                        "x", "-tzip", "Yolo.zip", "-y");

                ExecuteTaskItem executeTask = WSDLBuilder.buildExecuteTaskItem(
                        "python",
                        "test_images.py", "params.py");

                ArrayOfTaskItem tasks = new ArrayOfTaskItem();
                tasks.setTaskItem(new TaskItem[]{unzipTask, executeTask});

                Job job = new Job();
                job.setInputFiles(inputFiles);
                job.setOutputFiles(outputFiles);
                job.setTasks(tasks);
                job.setReservationId(myid);

                String jobId = services.submitJobWait(applicationId, job);

                assert jobId != null;

                String termination_status = services.waitJobTermination(applicationId, jobId);

                assertEquals(STATUS_COMPLETED, termination_status);
            } finally {
                boolean deleted = services.abortApplication(applicationId);
                assert deleted;
            }
        } finally {
            if (services.getError() != null) {
                System.out.println(services.getError());
            }
        }
    }

    @Test
    public void testPython() {
        AnekaWebServices services = new AnekaWebServices(Credentials.URL, true);
        String myid = UUID.randomUUID().toString();

        System.out.println(myid);

        try {
            boolean check;
            check = services.authenticateUser(Credentials.USERNAME, Credentials.PASSWORD);

            assert check;

            FTPStorageBucket bucket = new FTPStorageBucket(
                    "FTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.HOST);

            String applicationId = services.createApplicationWait(APP_NAME, bucket);

            assert applicationId != null;

            try {
                ApplicationResult result = services.queryApplication(applicationId);

                assert result.getCreatedDateTime().before(new Date());
                assert result.getDisplayName().equals(APP_NAME);
                assert result.isUseFileTransfer();
                assert result.getJobs().getJobResult().length == 0;
                assert result.getFinishedDateTime() == null;


                ArrayOfFile outputFiles = WSDLBuilder.buildArrayOfFile(
                        "FTP",
                        "/" + myid + "/output.txt",
                        "std.out");

                ExecuteTaskItem executeTask = WSDLBuilder.buildExecuteTaskItem(
                        "python",
                        "--version");

                ArrayOfTaskItem tasks = new ArrayOfTaskItem();
                tasks.setTaskItem(new TaskItem[]{executeTask});

                Job job = new Job();
                job.setOutputFiles(outputFiles);
                job.setTasks(tasks);
                job.setReservationId(myid);

                String jobId = services.submitJobWait(applicationId, job);

                assert jobId != null;

                String termination_status = services.waitJobTermination(applicationId, jobId);

                assertEquals(STATUS_COMPLETED, termination_status);

                SimpleFTPClient ftpClient = new SimpleFTPClient(bucket);
                String out = ftpClient.getString("/" + myid + "/output.txt");

                System.out.println(out);

                assert out.contains("Python");

            } finally {
                boolean deleted = services.abortApplication(applicationId);
                assert deleted;
            }
        } finally {
            if (services.getError() != null) {
                System.out.println(services.getError());
            }
        }
    }


    @Test
    public void test4() {
        AnekaWebServices services = new AnekaWebServices(Credentials.URL, true);
        String myid = UUID.randomUUID().toString();

        System.out.println(myid);

        try {
            boolean check;
            check = services.authenticateUser(Credentials.USERNAME, Credentials.PASSWORD);

            assert check;

            FTPStorageBucket bucket = new FTPStorageBucket(
                    "FTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.HOST);

            SimpleFTPClient ftpClient = new SimpleFTPClient(bucket);

            String folder = "/" + myid;
            String inputPath = folder + "/input.txt";
            String outputPath = folder + "/output.txt";
            String mString = getRandomString(256);

            assert ftpClient.connect();
            assert ftpClient.mkdir(folder);
            assert ftpClient.putString(inputPath, mString);

            String applicationId = services.createApplicationWait(APP_NAME, bucket);

            assert applicationId != null;

            try {
                ApplicationResult result = services.queryApplication(applicationId);

                assert result.getCreatedDateTime().before(new Date());
                assert result.getDisplayName().equals(APP_NAME);
                assert result.isUseFileTransfer();
                assert result.getJobs().getJobResult().length == 0;
                assert result.getFinishedDateTime() == null;

                ArrayOfFile inputFiles = WSDLBuilder.buildArrayOfFile(
                        "FTP",
                        inputPath,
                        "input.txt");

                ArrayOfFile outputFiles = WSDLBuilder.buildArrayOfFile(
                        "FTP",
                        outputPath,
                        "output.txt");

                CopyTaskItem copyTaskItem = new CopyTaskItem();
                copyTaskItem.setSource("input.txt");
                copyTaskItem.setTarget("output.txt");

                ArrayOfTaskItem tasks = new ArrayOfTaskItem();
                tasks.setTaskItem(new TaskItem[]{copyTaskItem});

                Job job = new Job();
                job.setInputFiles(inputFiles);
                job.setOutputFiles(outputFiles);
                job.setTasks(tasks);
                job.setReservationId(myid);

                String jobId = services.submitJobWait(applicationId, job);

                assert jobId != null;

                String termination_status = services.waitJobTermination(applicationId, jobId);

                assertEquals(STATUS_COMPLETED, termination_status);

                String outString = ftpClient.getString(outputPath);

                assertEquals(mString, outString);

            } finally {
                boolean deleted = services.abortApplication(applicationId);
                assert deleted;
            }
        } finally {
            if (services.getError() != null) {
                System.out.println(services.getError());
            }
        }
    }
    private static final String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";

    private static String getRandomString(final int sizeOfRandomString)
    {
        final Random random=new Random();
        final StringBuilder sb=new StringBuilder(sizeOfRandomString);
        for(int i=0;i<sizeOfRandomString;++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}