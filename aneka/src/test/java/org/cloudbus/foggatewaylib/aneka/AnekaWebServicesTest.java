package org.cloudbus.foggatewaylib.aneka;

import org.cloudbus.foggatewaylib.aneka.wsdl.ApplicationResult;
import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfFile;
import org.cloudbus.foggatewaylib.aneka.wsdl.ArrayOfTaskItem;
import org.cloudbus.foggatewaylib.aneka.wsdl.CopyTaskItem;
import org.cloudbus.foggatewaylib.aneka.wsdl.ExecuteTaskItem;
import org.cloudbus.foggatewaylib.aneka.wsdl.Job;
import org.cloudbus.foggatewaylib.aneka.wsdl.TaskItem;
import org.junit.Test;

import java.util.Date;
import java.util.UUID;

import static org.cloudbus.foggatewaylib.aneka.wsdl.JobStatus.STATUS_COMPLETED;
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
                    Credentials.LOCAL_IP,
                    9094);

            StorageBucket bucketOut = new FTPStorageBucket(
                    "OutputFTP",
                    Credentials.USERNAME,
                    Credentials.PASSWORD,
                    Credentials.LOCAL_IP,
                    9094);

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
                    Credentials.LOCAL_IP,
                    9094);

            ArrayOfFile sharedFiles = WSDLBuilder.buildArrayOfFile(
                    "FTP",
                    "",
                    "",
                    new String[]{"Yolo.zip"}
            );

//            ArrayOfFile sharedFiles = WSDLBuilder.buildArrayOfFile(
//                    "FTP",
//                    "/Yolo/",
//                    "",
//                    new String[]{
////                            "common/__init__.py",
////                            "common/utils.py",
////                            "common/data_transforms.py",
////                            "common/coco_dataset.py",
////                            "params.py",
////                            "test_images.py",
////                            "nets/model_main.py",
////                            "nets/yolo_loss.py",
////                            "nets/backbone/darknet.py",
////                            "nets/backbone/__init__.py",
////                            "nets/__init__.py",
//                            "data/coco.names",
////                            "training/params.py",
////                            "training/training.py",
////                            "evaluate/params.py",
////                            "evaluate/eval_coco.py",
////                            "evaluate/coco_index2category.json",
////                            "evaluate/eval.py",
////                            "weights/official_yolov3_weights_pytorch.pth",
//                    }
//            );

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
                        "C:\\Program Files\\7-Zip\\7z.exe",
                        "x", "-tzip", "Yolo.zip", "-y");

                ExecuteTaskItem executeTask = WSDLBuilder.buildExecuteTaskItem(
                        "python",
                        "test_images.py", "params.py");

//                ExecuteTaskItem executeTask = WSDLBuilder.buildExecuteTaskItem("dir");

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
}