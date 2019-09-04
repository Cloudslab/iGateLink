package org.cloudbus.foggatewaylib.aneka;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.manjrasoft.aneka.*;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Date;

public class AnekaWebServices {
    private TaskService service;
    private UserCredential mUserCredential;
    private String error;
    private String defaultApplicationId;

    private int requestTimeout = 5000;
    private int jobTimeout = 60000;
    private int pollingPeriod = 500;

    public AnekaWebServices(String url){
        this(url, false);
    }

    public AnekaWebServices(String url, boolean debug){
        service = new TaskService(url, 2, debug);
    }

    public boolean authenticateUser(String username, String password){
        AuthenticateUserResponse response;

        AuthenticateUser authenticateUserParams = new AuthenticateUser();
        authenticateUserParams.setUsername(username);
        authenticateUserParams.setPassword(password);

        try {
             response = service.authenticateUser(authenticateUserParams);
             mUserCredential = response.getAuthenticateUserResult().getUserCredential();
             dumpError(response.getAuthenticateUserResult());
             return response.getAuthenticateUserResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    @Nullable
    public String createApplication(String name, ArrayOfFile sharedFiles,
                                    StorageBucket... storageBuckets){
        CreateApplicationResponse response;

        CreateApplication createApplicationParams = new CreateApplication();
        ApplicationSubmissionRequest applicationSubmissionRequest = new ApplicationSubmissionRequest();
        applicationSubmissionRequest.setDisplayName(name);
        applicationSubmissionRequest.setSharedFiles(sharedFiles);
        applicationSubmissionRequest.setUserCredential(mUserCredential);
        createApplicationParams.setRequest(applicationSubmissionRequest);

        if (storageBuckets.length > 0){
            PropertyGroup metadata = new PropertyGroup();
            metadata.setNameProperty("Metadata");
            ArrayOfPropertyGroup metadataArray = new ArrayOfPropertyGroup();

            PropertyGroup buckets = new PropertyGroup();
            buckets.setNameProperty("StorageBuckets");
            PropertyGroup[] bucketGroups = new PropertyGroup[storageBuckets.length];

            for (int i = 0; i< storageBuckets.length; i++){
                bucketGroups[i] = storageBuckets[i].asPropertyGroup();
            }
            ArrayOfPropertyGroup arrayOfPropertyGroup = new ArrayOfPropertyGroup();
            arrayOfPropertyGroup.setPropertyGroup(bucketGroups);
            buckets.setGroups(arrayOfPropertyGroup);

            metadataArray.setPropertyGroup(new PropertyGroup[]{buckets});
            metadata.setGroups(metadataArray);

            applicationSubmissionRequest.setMetadata(metadata);
        }

        try {
             response = service.createApplication(createApplicationParams);
             dumpError(response.getCreateApplicationResult());
             if (response.getCreateApplicationResult().isSuccess()){
                 String applicationId = response.getCreateApplicationResult().getApplicationId();
                 defaultApplicationId = applicationId;
                 return applicationId;
             } else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public ApplicationResult queryApplication(String applicationId){
        QueryApplicationResponse response;

        QueryApplication queryApplicationParams = new QueryApplication();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationParams.setRequest(applicationQueryRequest);

        try {
             response = service.queryApplication(queryApplicationParams);
            dumpError(response.getQueryApplicationResult());
             return response.getQueryApplicationResult();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public ApplicationResult queryApplication(){
        return queryApplication(defaultApplicationId);
    }

    @Nullable
    public String queryApplicationStatus(String applicationId){
        QueryApplicationStatusResponse response;

        QueryApplicationStatus queryApplicationStatusParams = new QueryApplicationStatus();
        ApplicationQueryRequest applicationQueryRequest = new ApplicationQueryRequest();
        applicationQueryRequest.setApplicationId(applicationId);
        applicationQueryRequest.setUserCredential(mUserCredential);
        queryApplicationStatusParams.setRequest(applicationQueryRequest);

        try {
            response = service.queryApplicationStatus(queryApplicationStatusParams);
            dumpError(response.getQueryApplicationStatusResult());
            if(response.getQueryApplicationStatusResult().getStatus() != null)
                return response.getQueryApplicationStatusResult().getStatus().getValue();
             else
                 return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public String queryApplicationStatus(){
        return queryApplicationStatus(defaultApplicationId);
    }

    public boolean abortApplication(String applicationId){
        AbortApplicationResponse response;

        AbortApplication abortApplicationParams = new AbortApplication();
        ApplicationAbortRequest applicationAbortRequest = new ApplicationAbortRequest();
        applicationAbortRequest.setApplicationId(applicationId);
        applicationAbortRequest.setUserCredential(mUserCredential);
        abortApplicationParams.setRequest(applicationAbortRequest);

        try {
            response = service.abortApplication(abortApplicationParams);
            dumpError(response.getAbortApplicationResult());
            return response.getAbortApplicationResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    public boolean abortApplication(){
        return abortApplication(defaultApplicationId);
    }

    public boolean waitApplicationCreation(String applicationId){
        long stopTime = new Date().getTime() + requestTimeout;
        while(true){
            String status = queryApplicationStatus(applicationId);
            if (status == null)
                status = "NULL";

            switch (status){
                case ApplicationStatus.STATUS_SUBMITTED:
                case ApplicationStatus.STATUS_RUNNING:
                    return true;

                case "NULL":
                case ApplicationStatus.STATUS_UNSUBMITTED:
                    if (requestTimeout > 0 && new Date(stopTime).before(new Date())){
                        error = "Timeout";
                        return false;
                    } else
                        break;

                case ApplicationStatus.STATUS_FINISHED:
                case ApplicationStatus.STATUS_PAUSED:
                case ApplicationStatus.STATUS_ERROR:
                case ApplicationStatus.STATUS_UNKNOWN:
                default:
                    return false;
            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return false;
                }
            }
        }
    }

    public boolean waitApplicationCreation(){
        return waitApplicationCreation(defaultApplicationId);
    }

    @Nullable
    public String createApplicationWait(String name, ArrayOfFile sharedFiles,
                                        StorageBucket... storageBuckets){
        String id = createApplication(name, sharedFiles, storageBuckets);

        if (id == null)
            return null;

        if (waitApplicationCreation(id)){
            return id;
        } else {
            return null;
        }
    }

    @Nullable
    public String createApplicationWait(String name, StorageBucket... storageBuckets){
        return createApplicationWait(name, null, storageBuckets);
    }

    @NonNull
    private String[] __submitJobs(String applicationId, Job... jobs)
            throws IOException, XmlPullParserException{
        if (jobs.length == 0)
            return new String[jobs.length];

        SubmitJobsResponse response;

        SubmitJobs submitJobsParams = new SubmitJobs();
        JobSubmissionRequest jobSubmissionRequest = new JobSubmissionRequest();
        jobSubmissionRequest.setApplicationId(applicationId);
        jobSubmissionRequest.setUserCredential(mUserCredential);
        ArrayOfJob arrayOfJob = new ArrayOfJob();
        arrayOfJob.setJob(jobs);
        jobSubmissionRequest.setJobs(arrayOfJob);
        submitJobsParams.setRequest(jobSubmissionRequest);

        response = service.submitJobs(submitJobsParams);
        dumpError(response.getSubmitJobsResult());
        if(response.getSubmitJobsResult().isSuccess())
            return response.getSubmitJobsResult().getIds().getString();
        else
            return new String[jobs.length];
    }

    @NonNull
    public String[] submitJobs(String applicationId, Job... jobs) {

        try{
            return __submitJobs(applicationId, jobs);
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return new String[jobs.length];
        }
    }

    /**
     * Submits the given {@code jobs} to the default application.
     *
     * @see #submitJob(String, Job)
     */
    @NonNull
    public String[] submitJobs(Job... jobs) {
        return submitJobs(defaultApplicationId, jobs);
    }

    private int countNulls(Object... objs){
        int n = 0;
        for (Object o:objs)
            if (o == null)
                n++;
        return n;
    }

    /**
     * Submits the given {@code jobs} to the application identified by the given
     * {@code applicationId}, retrying any failing jobs every {@link #pollingPeriod} milliseconds
     * up to a maximum time of {@link #requestTimeout} milliseconds.
     * In case of error, the error message will be saved in the attribute {@link #error},
     * accessible through {@link #getError()}.
     *
     * @param applicationId the id of the application.
     * @param jobs the jobs to be submitted.
     * @return an array of the same length as {@code jobs} containing the ids of the created jobs
     *         in the order they were given. If a job creation fails, its id will be {@code null}.
     * @see #submitJobs(String, Job...)
     */
    @NonNull
    public String[] submitJobsWait(String applicationId, Job... jobs) {
        long stopTime = new Date().getTime() + requestTimeout;
        try{
            String[] result = new String[jobs.length];
            int nNulls;

            while ((nNulls = countNulls(result)) != 0) {
                Job[] remainingJobs = new Job[nNulls];

                for (int i = 0, j = 0; i < jobs.length; i++){
                    if (result[i] == null)
                        remainingJobs[j++] = jobs[i];
                }

                String[] retryResult = __submitJobs(applicationId, remainingJobs);

                for (int i = 0, j = 0; i < jobs.length; i++){
                    if (result[i] == null)
                        result[i] = retryResult[j++];
                }

                if (requestTimeout > 0 && new Date(stopTime).before(new Date())){
                    error = "Timeout";
                    return new String[jobs.length];
                }
                if (pollingPeriod > 0){
                    try {
                        Thread.sleep(pollingPeriod);
                    } catch (InterruptedException e){
                        e.printStackTrace();
                        dumpError(e);
                        return new String[jobs.length];
                    }
                }
            }
            return result;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return new String[jobs.length];
        }
    }

    @NonNull
    public String[] submitJobsWait(Job... jobs) {
        return submitJobsWait(defaultApplicationId, jobs);
    }

    @Nullable
    public JobResult queryJob(String applicationId, String jobId){
        QueryJobResponse response;

        QueryJob queryJobParams = new QueryJob();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJob(queryJobParams);
            dumpError(response.getQueryJobResult());
            if(response.getQueryJobResult().isSuccess())
                return response.getQueryJobResult();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public JobResult queryJob(String jobId) {
        return queryJob(defaultApplicationId, jobId);
    }

    @Nullable
    public String queryJobStatus(String applicationId, String jobId){
        QueryJobStatusResponse response;

        QueryJobStatus queryJobStatusParams = new QueryJobStatus();
        JobQueryRequest jobQueryRequest = new JobQueryRequest();
        jobQueryRequest.setJobId(jobId);
        jobQueryRequest.setApplicationId(applicationId);
        jobQueryRequest.setUserCredential(mUserCredential);
        queryJobStatusParams.setRequest(jobQueryRequest);

        try {
            response = service.queryJobStatus(queryJobStatusParams);
            dumpError(response.getQueryJobStatusResult());
            if(response.getQueryJobStatusResult().getStatus() != null)
                return response.getQueryJobStatusResult().getStatus().getValue();
            else
                return null;
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return null;
        }
    }

    @Nullable
    public String queryJobStatus(String jobId) {
        return queryJobStatus(defaultApplicationId, jobId);
    }

    public boolean abortJob(String applicationId, String jobId){
        AbortJobResponse response;

        AbortJob abortJobParams = new AbortJob();
        JobAbortRequest abortRequest = new JobAbortRequest();
        abortRequest.setJobId(jobId);
        abortRequest.setApplicationId(applicationId);
        abortRequest.setUserCredential(mUserCredential);

        try {
            response = service.abortJob(abortJobParams);
            dumpError(response.getAbortJobResult());
            return response.getAbortJobResult().isSuccess();
        } catch (IOException|XmlPullParserException e) {
            e.printStackTrace();
            dumpError(e);
            return false;
        }
    }

    @Nullable
    public boolean abortJob(String jobId) {
        return abortJob(defaultApplicationId, jobId);
    }

    @Nullable
    public String submitJob(String applicationId, Job job){
        return submitJobs(applicationId, job)[0];
    }

    @Nullable
    public String submitJob(Job job){
        return submitJob(defaultApplicationId, job);
    }

    @Nullable
    public String submitJobWait(String applicationId, Job job){
        return submitJobsWait(applicationId, job)[0];
    }

    @Nullable
    public String submitJobWait(Job job){
        return submitJobWait(defaultApplicationId, job);
    }

    public String waitJobTermination(String applicationId, String jobId){
        long stopTime = new Date().getTime() + jobTimeout;
        while(true){
            String status = queryJobStatus(applicationId, jobId);
            if (status == null)
                status = "NULL";

            switch (status){
                case JobStatus.STATUS_QUEUED:
                case JobStatus.STATUS_RUNNING:
                case JobStatus.STATUS_STAGINGIN:
                case JobStatus.STATUS_STAGINGOUT:
                    if (jobTimeout > 0 && new Date(stopTime).before(new Date())){
                        return "Timeout";
                    } else
                        break;

                case JobStatus.STATUS_COMPLETED:
                case JobStatus.STATUS_ABORTED:
                case JobStatus.STATUS_FAILED:
                case JobStatus.STATUS_REJECTED:
                case JobStatus.STATUS_UNKNOWN:
                default:
                    return status;

            }

            if (pollingPeriod > 0){
                try {
                    Thread.sleep(pollingPeriod);
                } catch (InterruptedException e){
                    e.printStackTrace();
                    dumpError(e);
                    return "ERROR";
                }
            }
        }
    }

    @Nullable
    public String waitJobTermination(String jobId){
        return waitJobTermination(defaultApplicationId, jobId);
    }

    private void dumpError(Result result){
        if (result != null && result.getError() != null){
            error = result.getError().getMessage();
        } else{
            error = null;
        }
    }

    private void dumpError(Exception e){
        error = e.getMessage();
    }

    public String getError() {
        return error;
    }

    public UserCredential getUserCredential() {
        return mUserCredential;
    }

    public void setUserCredential(UserCredential mUserCredential) {
        this.mUserCredential = mUserCredential;
    }

    public String getDefaultApplicationId() {
        return defaultApplicationId;
    }

    public void setDefaultApplicationId(String defaultApplicationId) {
        this.defaultApplicationId = defaultApplicationId;
    }

    public int getRequestTimeout() {
        return requestTimeout;
    }

    public void setRequestTimeout(int requestTimeout) {
        this.requestTimeout = requestTimeout;
    }

    public int getJobTimeout() {
        return jobTimeout;
    }

    public void setJobTimeout(int jobTimeout) {
        this.jobTimeout = jobTimeout;
    }

    public int getPollingPeriod() {
        return pollingPeriod;
    }

    public void setPollingPeriod(int pollingPeriod) {
        this.pollingPeriod = pollingPeriod;
    }

    public boolean isLoggedIn(){
        return mUserCredential != null;
    }

    public boolean hasValidDefaultApplication(){
        return defaultApplicationId != null;
    }
}
