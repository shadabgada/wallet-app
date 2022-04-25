# wallet-app

Running an application

1. mvn clean package
2. cd target
3. java -jar wallet-0.0.1-SNAPSHOT.jar

### User credentials
As spring security is implemented, username and password needs to be passed using `Basic Auth`. The credentials can be be found in data.sql file. 
For example, User with name `Scott Parker` has **username**:`scottparker`, **password**:`scott@123`, and **player id**:`12`

### Endpoints

**Deposit Amount**
```
POST: localhost:8080/wallet/deposit
Request body:
{
    "transactionId": 10,
    "playerId": 12,
    "amount": 50
}
```
**Place bet**
```
POST: localhost:8080/wallet/bet
Request body:
{
    "transactionId": 11,
    "playerId": 12,
    "amount": 200,
    "betId":1
}
```

**Register Win**
```
POST: localhost:8080/wallet/win
Request body:
{
    "transactionId": 12,
    "playerId": 12,
    "amount": 600,
    "betId": 1
}
```

**Withdraw Amount**
```
POST: localhost:8080/wallet/withdraw
Request body:
{
    "transactionId": 13,
    "playerId": 12,
    "amount": 400
}
```

**Check balance**
```
GET: localhost:58497/wallet/balance/12

```

**Get All transactions**
```
As this endpoint can be accessed by only admin, 
use below user that has admin authority.
username:jasonparker
password:jason@123
```
```
GET: localhost:8080/wallet/transactions

Sorting and pagination is supported.

For pagination, below are valid query params:
page: required page no
size: page size
and all column names of transaction db are valid query params.

For sorting,
use sortBy query param.
For example, sortBy:Amount

```

### Further Improvements

- We need to use Bigdecimal for dealing with amounts. the current code makes use of double.
- Additional JUnit tests needs to be added. Specifically, controller tests to test the overall workflow and response status codes for the requests.

