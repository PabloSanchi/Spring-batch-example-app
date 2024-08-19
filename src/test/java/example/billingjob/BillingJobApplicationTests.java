package example.billingjob;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest
@SpringBatchTest
@Testcontainers
@ActiveProfiles("dev")
@ExtendWith(OutputCaptureExtension.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BillingJobApplicationTests {

	@Container
	@ServiceConnection
	public static PostgreSQLContainer<?> dbContainer = new PostgreSQLContainer<>("postgres:13.3-alpine")
			.withDatabaseName("postgres")
			.withUsername("postgres")
			.withPassword("postgres");

	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;

	@Autowired
	private JobRepositoryTestUtils jobRepositoryTestUtils;

	@AfterEach
	public void setUp() {
		jobRepositoryTestUtils.removeJobExecutions();
	}

	@Test
	void testJobExecution(CapturedOutput output) throws Exception {
		String filePath = "test/file/path";

		JobParameters jobParameters = this.jobLauncherTestUtils.getUniqueJobParametersBuilder()
				.addString("input.file", filePath)
				.toJobParameters();

		JobExecution jobExecution = this.jobLauncherTestUtils.launchJob(jobParameters);

		Assertions.assertTrue(output.getOut().contains("processing billing information from file " + filePath));
		Assertions.assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
	}

}
