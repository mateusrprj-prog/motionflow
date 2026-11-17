# MotionFlow — Contexto do Projeto

## Objetivo
Construir um editor Android de motion graphics e animação por keyframes, com canvas, timeline, efeitos, áudio/vídeo, recursos 3D, IA local e exportação.

## Ambiente de desenvolvimento
- Desenvolvimento pelo POCO X7 Pro (Android).
- GitHub como repositório e ambiente de build.
- GitHub Actions gera o APK debug como artefato.
- Não exigir PC nem Android Studio instalado localmente.

## Referência técnica
Pacote analisado: Keyflow 1.6.0, arquivo APKS fornecido nesta conversa.
A inspeção inicial identificou:
- base APK e splits ARM64, português, espanhol e densidade xxhdpi.
- Três arquivos DEX no APK base.
- Evidências de Kotlin/Jetpack Compose, MediaPipe, TensorFlow Lite, Rhino JavaScript e OpenGL/GLSL.
- Modelos binários em assets/depth e assets/roto.
- Referências a keyframes, editor de curvas, expressões, efeitos, rig 3D, bones, morph targets e física.
- Sistema de plugins KFPlug com shaders GLSL ES 1.00.

Não copiar código, modelos ou assets proprietários. Bibliotecas e modelos externos só entram após verificação de licença, compatibilidade e origem.

## Decisões
- Nome: MotionFlow.
- Package: app.motionflow.
- Kotlin + Jetpack Compose + Material 3.
- minSdk 26, targetSdk 35, compileSdk 35.
- JDK 17, AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9.
- Arquitetura inicial separada em model, viewmodel e ui.
- Tema escuro.
- Sem anúncios ou compras na fase inicial.
- Build em .github/workflows/build.yml.

## Etapa 1
- Projeto Gradle Kotlin DSL e Version Catalog.
- Activity única com Jetpack Compose.
- Home mobile com projetos recentes.
- Criação básica de projetos em proporções 9:16, 1:1, 16:9 e 4:5.
- GitHub Actions para gerar APK debug e publicar artefato.

## Limitações conhecidas
- Projetos ficam em memória; persistência ainda não implementada.
- Tela inicial ainda não abre o editor.
- Ainda não existe canvas, timeline, renderização GL, mídia ou exportação.
- Build aguardando validação do GitHub Actions após o commit do código.

## Próxima etapa
Etapa 2: modelo de dados versionado, salvar/abrir projeto, persistência local e Photo Picker.
