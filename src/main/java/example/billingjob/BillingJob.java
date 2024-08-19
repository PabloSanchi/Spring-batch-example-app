package example.billingjob;

import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;

public class BillingJob implements Job {

    private static String JOB_NAME = "BillingJob";

    private final JobRepository jobRepository;

    public BillingJob(final JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public String getName() {
        return JOB_NAME;
    }

    @Override
    public void execute(JobExecution execution) {
        JobParameters jobParameters = execution.getJobParameters();
        String inputFile = jobParameters.getString("input.file");
        System.out.println("processing billing information from file " + inputFile);
        execution.setStatus(BatchStatus.COMPLETED);
        execution.setExitStatus(ExitStatus.COMPLETED);
        this.jobRepository.update(execution);
    }
}
