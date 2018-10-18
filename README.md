<a><img src="https://avatars2.githubusercontent.com/u/673103?s=280&v=4" title="Entando" width="100"></a>
<a><img src="https://blobscdn.gitbook.com/v0/b/gitbook-28427.appspot.com/o/spaces%2F-LC2AYrI9MJa-_aAjE1u%2Favatar.png?generation=1525840515697023&alt=media" title="Entando" width="100" hspace="40"></a>

# Pact tests

Implementation of tests for API contracts based on Pact between the Entando App Builder Application (Consumer) and the Entando MAPP Engine Application (Provider).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

If you haven't got Docker installed,
    [follow these installation instructions](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-on-ubuntu-18-04).

The container runs in the background and incrementally rebuilds the site each
time a file changes. The container runs in the foreground, but
you can use `CTRL+C` to get the command prompt back.

### Running consumer tests

1.  Setup the appbuilder container.

    a. Pull the appbuilder image by running:
    
       ```bash
       docker pull entando/appbuilder:5.0.1-SNAPSHOT
       ```
    b. Run the image by running:
    
       ```bash
       docker run -it -p 5000:5000 -e DOMAIN=http://localhost:8080/entando entando/appbuilder:5.0.1-SNAPSHOT
       ```
2.  Set this `Dwebdriver.chrome.driver=/home/ampie/chromedriver` as webdriver JVM parameter, then you can just run the tests from your IDE.
    
### Running provider tests

1.  Setup the appbuilder container.

    a. Pull the entando-engine image by running:
    
       ```bash
       docker pull entando/engine-api
       ```
    b. Run the image by running:
    
       ```bash
       docker run -it -p 8080:8080 -e DOMAIN=http://localhost:8080/entando entando/engine-api
       ```
        
       
       
