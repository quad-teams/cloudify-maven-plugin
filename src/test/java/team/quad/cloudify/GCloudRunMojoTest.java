package team.quad.cloudify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.apache.maven.shared.utils.StringUtils.unifyLineSeparators;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class GCloudRunMojoTest {

  private static final String DESTINATION_DIR = "target/test-classes";
  private GCloudRunMojo mojo;

  @BeforeEach
  void setup() {
    MavenProject project = new MavenProject();
    project.setArtifactId("gcloud-run-mojo");

    mojo = new GCloudRunMojo();
    mojo.setProjectSuffix("suffix");
    mojo.setDestinationDir(DESTINATION_DIR);
    mojo.setProjectId("gcloud-mojo");
    mojo.setProject(project);
  }

  @Test
  void test_destination_dir_is_informed() {
    assertThatExceptionOfType(MojoExecutionException.class)
      .isThrownBy(() -> {
        mojo.setDestinationDir(null);
        mojo.execute();
      });

    assertThatExceptionOfType(MojoExecutionException.class)
      .isThrownBy(() -> {
        mojo.setDestinationDir(" ");
        mojo.execute();
      });
  }

  @Test
  void test_files_were_copied_with_replacements() throws MojoExecutionException, IOException {
    mojo.execute();
    assertEqualFiles("/gcp/expected-cloudbuild.yaml", DESTINATION_DIR + "/cloudbuild.yaml");
    assertEqualFiles("/gcp/expected-deploy", DESTINATION_DIR + "/deploy");
    assertEqualFiles("/gcp/expected-deploy.cmd", DESTINATION_DIR + "/deploy.cmd");
    assertEqualFiles("/gcp/expected-dockerfile", DESTINATION_DIR + "/Dockerfile");
  }

  private void assertEqualFiles(String origin, String destination) throws IOException {
    String originContent = unifyLineSeparators(IOUtils.toString(getClass().getResourceAsStream(origin), StandardCharsets.UTF_8));
    String destinationContent = unifyLineSeparators(FileUtils.readFileToString(new File(destination), StandardCharsets.UTF_8));
    Assertions.assertThat(originContent).isEqualTo(destinationContent);
  }
}
