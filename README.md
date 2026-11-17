# MotionFlow — Build por ZIP

O repositório mantém somente o workflow e esta documentação. O código-fonte fica em um arquivo ZIP anexado a uma GitHub Release.

## Preparar a fonte (uma vez por versão)

1. Abra **Releases** neste repositório e crie uma release com a tag `source-v1`.
2. Anexe o arquivo `MotionFlow-source.zip` como asset da release.
3. Publique a release.

O ZIP deve conter na raiz do arquivo `settings.gradle.kts`, `build.gradle.kts`, `gradle/libs.versions.toml` e a pasta `app/`.

## Gerar o APK pelo celular

1. Abra **Actions → MotionFlow - Build APK from ZIP**.
2. Toque em **Run workflow**.
3. Em `release_tag`, use `source-v1` (ou outra tag que tenha o mesmo asset).
4. Execute o workflow.
5. Ao terminar, abra a execução e baixe o arquivo em **Artifacts**.

O workflow baixa o ZIP da Release, valida e extrai o conteúdo em uma pasta temporária do runner, compila com Java 17 / Android SDK 35 / Gradle 8.9 e publica o APK debug como artefato. O ZIP de código-fonte não é commitado na branch.

## Observações

- Não envie APK nem código-fonte para a branch `main`.
- O asset da Release deve se chamar exatamente `MotionFlow-source.zip`.
- O build requer que o ZIP contenha um projeto Android Gradle válido.
- Nenhum PC é necessário: os passos podem ser feitos pelo navegador do Android.
