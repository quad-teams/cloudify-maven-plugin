![Cloudify Maven Plugin](banner-logo-small.png)

# Cloudify - The fastest way to the cloud

No developer has ever expressed the desire to write more configuration and less code. While it's much easier to get code to the cloud these days, there's still a configuration hurdle to overcome. `couldify-maven-plugin` attempts to make it even easier to prepare your project for the cloud and deploy it.

Supported clouds:

- Google Cloud Run

More to come in the future.

## Installing / Getting started
### Google Cloud Run

Execute the following in the root of your project:

```shell
mvn -N team.quad:cloudify-maven-plugin:0.1.0:gcloud-run
```
This generates the following:
- Docker file.
- Google Cloud Run build and deploy configuration file.
- First-time setup scripts to make creating a Cloud Run service even simpler.

Next step is to deploy to the cloud:

```shell
./deploy
```

or on Windows:

```bash
deploy.cmd
```

# Building

```shell
mvn clean install
```

## Licensing

The code in this project is licensed under MIT license.
