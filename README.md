# MyHealth+

MyHealth+ is a secure, web-based application designed for patients to manage their own record. It is implemented using features such as Responsive Design, Single Page Applications (SPAs), Model View View Model pattern (MVVM) and RESTful Services. This creates a solution that provides an enhanced user experience and improved set of features.

MyHealth+ is split into two parts, the server side and the front-end. The server side exposes FHIR RESTful API and a Token-Based Authentication Service. 
The front-end UI processes FHIR Resources and take care of rendering the content based on the device’s dimension.

# Solution Overview
The solution chosen is based on SPAs (Single Page Applications). SPA is a technique that dynamically updates the HTML page without constantly reloads. After the first page is loaded, all interaction with the server happens through AJAX calls, usually JSON format. The client side (JavaScript framework) takes care of updating the view based on the user actions. This creates a smooth and responsive web application where the user does not have to wait for the page to refresh. 

Another benefit is how the architecture is layered. SPA creates a separation between the presentation and the service layer. This makes easy to maintains and scale the application without touching the complete platform. It also allows to rolling updates or new features quickly without big outages.

The following figure depicts the high-level architecture diagram.

![High Level Architecture Diagram]
(https://github.com/jmiddleton/myhealth/blob/master/docs/images/hlad.png)

The solution is composed of two main components:

•	FHIR RESTful API built on top of Spring MVC and Spring Boot. It mains responsibilities are:
o	Exposes RESTful endpoints based on FHIR specification.
o	Access PCEHR B2M Services to retrieve patient information.
o	Mappings B2M responses to FHIR Resources.
o	Token-based Authentication to secure every request to the server.
o	In-memory caching for High Availability
•	Static HTML5 website with AngularJS and CSS3. The responsibilities of this layer are:
o	User Authentication.
o	Device detection.
o	Consumes FHIR Resources from the server side.
o	Control the navigation flow.

# Deployment topology
The application has been architected in a way that can be easily deployed in any type of infrastructure either on-premises or cloud (as mentioned below). The front-end can be hosted on any Web Server that can serve static content. The server side can run on any JEE Container or using Spring Boot standalone.

The architecture makes really simple delivering new releases or updates. We can use blue-green deployment or rolling updates to some servers while keeping running the actual one. Once the application is healthy, the old application can be removed. These two strategies provide Zero Downtime with minimum cost and operational effort.

The following diagram represents the deployment topology using Amazon AWS. 

![Deployment Topology]
(https://github.com/jmiddleton/myhealth/blob/master/docs/images/deploymentaws.png)

The deployment is divided in two areas:
•	The front-end is deployed on Amazon S3 Bucket. It only contains static resources (HTML5, CSS images, and JavaScript). CloudFront is used to provide fast access and caching of those resources.
•	The server side is a Java application installed in a Docker image that is deployed on Amazon Beanstalk.  Amazon Beanstalk deploys and manages the application in the AWS cloud providing features such as Load balancing, Auto Scaling, Virtual Private Cloud and others.

To make the application accessible to the end user, Route 53 is configured to redirect requests either to the REST API (http://myhealth-api.puntanegra.info) or the static content (http://myhealth.puntanegra.info).

The below components has been provisioned for the infrastructure:
Route 53 
Provides DNS services to simplify domain management.
Elastic Load Balancer 
ELB to spread traffic to Web Server Auto scaling groups. 

Scaling Web Tier 
Group of EC2 instances handling HTTP requests.  
ElastiCache 
Provides caching services for application.

Amazon S3 
Used for storing Static Object and Backups.
CloudFront
High Volume Content is edge cached using CloudFront. 
