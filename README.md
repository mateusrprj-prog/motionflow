# MotionFlow

Editor móvel de motion graphics para Android, desenvolvido em Kotlin e Jetpack Compose.

## Estrutura do repositório
- `main`: workflow de compilação e documentação.
- `source-archive`: código-fonte do aplicativo. Essa separação mantém a branch principal enxuta.

## Gerar o APK
O GitHub Actions compila automaticamente quando há atualização na branch `source-archive`.

1. Abra [Actions](../../actions).
2. Entre na execução mais recente de **MotionFlow - Build APK**.
3. Aguarde o resultado do job.
4. Quando concluir, baixe `MotionFlow-debug-<número>` em **Artifacts**.

O build usa Java 17, Android SDK 35 e Gradle 8.9. O APK debug fica disponível como artefato do Actions por 14 dias.

## Estado atual — 0.2.0-alpha
- Nova tela inicial baseada no pacote de interface do projeto.
- Projetos reais do ViewModel conectados à lista inicial.
- Abertura de projeto na tela do editor.
- Timeline horizontal, trilhas, seleção de camada, inspetor e painel de curvas.
- Adição de camadas de forma, texto, ajuste e câmera 3D.
- Importação de imagens/vídeos e áudio pelo seletor de documentos do Android.
- Desfazer/refazer básico para adição de camadas.

## Limitações
A prévia ainda é um placeholder. As camadas adicionadas e as URIs importadas ficam em memória nesta etapa; ainda faltam persistência, renderizador de mídia, reprodução real, keyframes funcionais, motor de curvas e exportação de vídeo.
