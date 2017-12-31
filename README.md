# inren-elevation

This is a private project. So don't expect to much.
The only purpose of this project is:
 * Getting overview of my finance.
 * Switching early to major updates of the frameworks before we switch at work so I get to know them first.
 * Experimenting on new stuff I like

As a working father, I can only work later the evening (until midnight) on this project so sometimes I do things, that are not well thought through. Oh yes, you must expect that some places of the code are bad. I allow me that, because I am the only one working with this code and I have to live with the fact that sometimes I am just to tired to program a perfect solution but that is something I have to live with.

If you think, you like my framework and want to use it also for your personal purpose, just fork it and have fun. If you think you would like to join me, send me an email. We can fork the project to one where we only commit stuff that is well thought trough. 

But until then, that is my playground.

Best regards,
    Ingo Renner    
 
## To get it run

There are 2 environment variables you have to set
 * private.data.home This is where your private data is stored. Backup of your categories and so on.
 * spring.profiles.active directory where the spring configuration is. This configuration I don't share here but you can take the example to create a personal one.
 
 
### Configuration example

In the directory for spring.profiles.active you have to place two files:

jpa.mysql.properties which contains the jpa settings:

> jpa.databasePlatform=org.hibernate.dialect.MySQL5Dialect

> jpa.showSql=false

> jpa.generateDdl=true
 
> hibernate.hbm2ddl.auto=create

 
 
and db.mysql.properties which cantains your database settings:

> jdbc.driverClassName=com.mysql.jdbc.Driver

> jdbc.url=jdbc:mysql://localhost:3306/databasename

> jdbc.username=username

> jdbc.password=password


 