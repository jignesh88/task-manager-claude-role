- [x] Implement MongoDB persistence layer
  - [x] refactor service layer, separate and move some func to persistence layer
  - [x] generate several hundreds of tasks so persistence layer can return them to the service layer
  - [x] introduce mongoDB
  - [x] seeding already prepared records to mongoDB


- [ ] Securing and Personalizing Task Management API
    - [x] add basic authentication
    - [x] add user support to MongoDB
    - [x] new endpoint for creating users with secure password handling
        - [x] password hashing with BCrypt
        - [x] prevent user enumeration
        - [x] automatic USER role assignment
        - [x] global exception handling
    - [x] assign tasks to users
    - [x] restrict access to tasks by user
    - [ ] use JWT tokens for authentication
    - [ ] enhance API security