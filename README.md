# MotionFlow

Editor Android de motion graphics, desenvolvido em Kotlin e Jetpack Compose.

## Código-fonte
O código do aplicativo fica na branch [source-archive](../../tree/source-archive). A branch `main` mantém o workflow de build e a documentação.

## Gerar o APK
O GitHub Actions inicia automaticamente uma compilação quando a branch `source-archive` recebe alterações.

1. Abra [Actions](../../actions).
2. Entre na execução mais recente de **MotionFlow - Build APK**.
3. Aguarde a conclusão.
4. Baixe o arquivo `MotionFlow-debug-<número>` na seção **Artifacts**.

O build usa Java 17, Android SDK 35 e Gradle 8.9. O APK debug fica disponível como artefato do Actions por 14 dias.

## Versão atual
O código-fonte está na versão `0.2.0-alpha`. Consulte o README da branch `source-archive` para o resumo dos recursos e limitações.
