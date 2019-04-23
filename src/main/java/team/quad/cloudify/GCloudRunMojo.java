package team.quad.cloudify;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
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
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.substringAfterLast;
import static org.fusesource.jansi.Ansi.ansi;

@Mojo(name = "gcloud-run")
public class GCloudRunMojo extends AbstractMojo {

  @Parameter(property = "destinationDir", defaultValue = ".")
  private String destinationDir;

  @Parameter(property = "projectSuffix")
  private String projectSuffix;

  @Parameter(property = "projectId")
  private String projectId;

  @Component
  private MavenProject project;

  public void execute() throws MojoExecutionException {
    if (StringUtils.isBlank(destinationDir)) {
      throw new MojoExecutionException("The parameter `destinationDir` " +
        "must contains the directory where the files will be generated");
    }

    Map<String, String> context = new HashMap<String, String>() {{
      put("artifactId", project.getArtifactId());
    }};

    if (StringUtils.isBlank(projectSuffix)) {
      projectSuffix = RandomStringUtils.randomNumeric(10);
    }

    if (StringUtils.isBlank(projectId)) {
      context.put("createProject", "true");
      projectId = project.getArtifactId() +
        "-" + projectSuffix;
    }

    context.putIfAbsent("createProject", "false");
    context.put("projectId", projectId);

    copyWithReplacements("/gcp/cloudbuild.yaml", context);
    copyWithReplacements("/gcp/deploy", context);
    copyWithReplacements("/gcp/deploy.cmd", context);
    copyWithReplacements("/gcp/Dockerfile", context);

    updateDockerIgnore();
    applyFilePermissions();
    printMessage();
  }

  private void copyWithReplacements(String name, Map<String, String> context) {
    try {
      InputStream origin = getClass().getResourceAsStream(name);
      File destination = new File(destinationDir, substringAfterLast(name, "/"));

      String content = IOUtils.toString(origin, StandardCharsets.UTF_8);
      String substitutedContent = StrSubstitutor.replace(content, context);

      FileUtils.writeStringToFile(destination, substitutedContent, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }

  private void updateDockerIgnore() {
    File dockerIgnoreFile = new File(".", ".dockerignore");

    if (dockerIgnoreFile.exists()) {
      try {
        String lineSeparator = System.getProperty("line.separator");
        String lines = lineSeparator + "!src/" +
          lineSeparator + "!pom.xml";

        FileUtils.writeStringToFile(dockerIgnoreFile, lines, StandardCharsets.UTF_8, true);
      } catch (IOException e) {
        throw new RuntimeException(e.getMessage(), e);
      }
    }
  }

  private void applyFilePermissions() {
    boolean deployExecutable = new File(".", "deploy").setExecutable(true);

    if (!deployExecutable) {
      getLog().info("Wasn't possible to give deploy execution permissions");
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

  public void setProjectSuffix(String projectSuffix) {
    this.projectSuffix = projectSuffix;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public void setProject(MavenProject project) {
    this.project = project;
  }
}
