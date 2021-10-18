# Eventarz-Events

A part of my Master's thesis - a simple web application for organizing events, consisting of 7 microservices:

- [Eventarz-Gateway](https://github.com/Atloas/Eventarz-Gateway) - contains full project description.
- [Eventarz-Application](https://github.com/Atloas/Eventarz-Application)
- [Eventarz-Users](https://github.com/Atloas/Eventarz-Users)
- [Eventarz-Groups](https://github.com/Atloas/Eventarz-Groups)
- [Eventarz-Events](https://github.com/Atloas/Eventarz-Events)
- [Eventarz-EurekaServer](https://github.com/Atloas/Eventarz-EurekaServer)
- [Eventarz-ZuulServer](https://github.com/Atloas/Eventarz-ZuulServer)

---

This microservice handles event data by accessing a dedicated database.
It exposes a REST API that in turn performs simple CRUD operations.
