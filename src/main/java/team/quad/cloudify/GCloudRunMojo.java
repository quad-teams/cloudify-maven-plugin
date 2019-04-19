package team.quad.cloudify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.fusesource.jansi.Ansi.ansi;

@Mojo(name = "gcloud-run")
public class GCloudRunMojo extends AbstractMojo {

  @Parameter(property = "destinationDir", defaultValue = ".")
  private String destinationDir;

  @Component
  private MavenProject project;

  public void execute() throws MojoExecutionException {
    if (StringUtils.isBlank(destinationDir)) {
      throw new MojoExecutionException("The parameter `destinationDir` " +
        "must contains the directory where the files will be generated");
    }

    Map<String, String> context = Collections
      .singletonMap("artifactId", project.getArtifactId());

    copyWithReplacements("/gcp/cloudbuild.yaml", context);
    copyWithReplacements("/gcp/deploy", context);
    copyWithReplacements("/gcp/deploy.cmd", context);
    copyWithReplacements("/gcp/Dockerfile", context);

    printMessage();
  }

  private void copyWithReplacements(String name, Map<String, String> context) {
    try {
      InputStream origin = getClass().getResourceAsStream(name);
      File destination = new File(destinationDir, substringAfterLast(name, "/"));

      String fileContent = IOUtils.toString(origin, StandardCharsets.UTF_8);
      String artifactId = StrSubstitutor.replace(fileContent, context);

      FileUtils.writeStringToFile(destination, artifactId, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void printMessage() {
    getLog().info("");
    getLog().info("========================================================================");
    getLog().info(ansi().a("Google Cloud Run has been enabled").toString());
    getLog().info(ansi().a("You can run ").bold().a("./deploy").boldOff()
      .a(" to deploy your project").toString());
    getLog().info("========================================================================");
    getLog().info("");
  }

  public void setDestinationDir(String destinationDir) {
    this.destinationDir = destinationDir;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }
}
