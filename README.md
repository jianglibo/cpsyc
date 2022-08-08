## Install

https://docs.spring.io/spring-boot/docs/current/reference/html/deployment.html#deployment.installing

```bash
./gradlew bootJar
```

ansible-playbook --vault-id mailhandler@/opt/mailhandler.pwd --user root --inventory inventory install-systemd.yml -vvv


## Encrypt ansible variable.

ansible-vault encrypt_string --vault-password-file /opt/mailhandler.pwd 'foobar' --name 'jasypt_pass'