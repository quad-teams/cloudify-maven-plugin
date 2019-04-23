![Cloudify Maven Plugin](banner-logo-small.png)

# Cloudify
> The fastest way to the cloud.

No developer has ever expressed the desire to write more configuration and less code. While it's much easier to get code to the cloud these days but there's still a configuration hurdle to overcome. `couldify-maven-plugin` attempts to make it even easier to prepare your project for the cloud and deploy it.

Supported clouds:

- Google Cloud Run

More to come in the future.

## Installing / Getting started

Execute the following in the root of your porject:

```shell
mvn -N team.quad:cloudify-maven-plugin:0.1.0:gcloud-run
```

# Building

```shell
mvn clean install
```

## Licensing

The code in this project is licensed under MIT license.
