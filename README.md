# Pact tests

Implementation of contract tests with Pact between the Entando App Builder Application (Consumer) and the Entando MAPP Engine Application (Provider).

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

Java, Maven, Selenium, Google Chrome, ChromeDriver and Docker installed on your machine

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
