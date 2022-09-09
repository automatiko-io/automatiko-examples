# Batch processing example

## Overview 

this is an example showing batch processing use cases backed by workflow. It uses Automatiko project to build self described service.

See complete description of this example [here](https://automatikio.com/component-main/0.0.0/examples/batch.html)

## Run it

It relies on files being dropped into a folder so that is what needs to be configured to run this example

`docker run -e DOCUMENTS_FOLDER=/automatiko/documents -v LOCAL_DIRECTORY:/automatiko/documents -p 8080:8080 automatiko/document-processor`

NOTE: `LOCAL_DIRECTORY` needs to be replaced with absolute path to be mounted as volume to the container.

once this is done you can see the fully described service at:

http://localhost:8080/q/swagger-ui/#/

In addition to that, service is equipped with tiny ui that helps to visualise the service data, can be accessed at 

http://localhost:8080/management/processes/ui

## Use it

There are two usage scenarios for this service

- process individual text files
- process archive of text files (zip)

Depending which file type is dropped into directory different process will be triggered.

Text files are also classified and based on classification different paths are invoked:

- greetings (text files that starts with `hello`)
- reports (text files with length more than 100)
- confidential (text files that have the file name `password.txt`)
- unclassified (all other text files)

Zip file can contain any classifiation types.

### Example

Create text file (`greeting.txt`) with content `hello world` and drop it into `LOCAL_DIRECTORY` that was mounted into the container.

The service will directly consume the file, process it and deletes it from the `LOCAL-DIRECTORY`.

Same can be done with archive files (zip) that include txt files. All other file types in the archive will be ignored.