# Further improvements

## Improvements to the code:

* I would segregate more the responsibilities between Transfer and Accounts.
* I would decouple the Repository logic that I had to implement for this solution and would place the code in the service layer.
* I would add Interfaces for all services.
* Adding more tests, also for repository methods.
* Changing to a real DB.
* Improving Logging.
* Saving in DB the transfer history.
* Further Javadoc documentation for all methods.
* Better error handling

## Improvements that could be added to make this application production ready:

* DB usage in order for the data to be persistent and provide scalability
* GitHub integration for the code to be shared between multiple developers
* GitLab integration for CI/CD
* Dockerization for easier deployment
* SonarLint with rules for developing cleaner code
* Implement monitoring tools such as Actuator
* Addition of Swagger service for endpoint validation