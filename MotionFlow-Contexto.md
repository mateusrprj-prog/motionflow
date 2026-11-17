# MotionFlow — Contexto do Projeto

## Objetivo
Construir um editor Android de motion graphics e animação por keyframes, com canvas, timeline, efeitos, áudio/vídeo, recursos 3D, IA local e exportação.

## Referência técnica analisada
Pacote fornecido: `Keyflow_1.6.0.apks`, versão indicada pelo nome do arquivo 1.6.0.
A inspeção inicial do bundle identificou:
- `base.apk` e splits para ARM64, português, espanhol e densidade xxhdpi.
- Código DEX em três arquivos no APK base.
- Evidências de Kotlin/Jetpack Compose, MediaPipe, TensorFlow Lite, Rhino JavaScript e OpenGL/GLSL.
- Modelos em `assets/depth/` (`depth_fast.tflite`, `depth_model.tflite`) e `assets/roto/magic_touch.tflite`.
- Referências a KeyframeStore, TransformValueStore, ExpressionStore, EffectStore, GraphEditor, rig 3D, bones, morph targets e física.
- Documentação interna sobre plugins `.kfplug`, com shaders GLSL ES 1.00 e manifests JSON.

Os modelos binários e componentes de terceiros não foram copiados para este projeto. Antes de reutilizar qualquer modelo, biblioteca ou recurso, verificar licença, origem e compatibilidade.

## Decisões
- Nome de trabalho: MotionFlow.
- Package: `app.motionflow`.
- Kotlin + Jetpack Compose + Material 3.
- minSdk 26, targetSdk 35, compileSdk 35.
- JDK 17, AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9.
- Arquitetura inicial separada em `model`, `viewmodel` e `ui`.
- Tema escuro, interface pensada para celular.
- Sem anúncios, cobrança ou dependência de rede para editar.

## Etapa 1 — Implementado
- Projeto Gradle Kotlin DSL e version catalog.
- Activity única com Jetpack Compose.
- Tema escuro e identidade visual inicial.
- Home com projetos recentes.
- Diálogo de novo projeto com proporções 9:16, 1:1, 16:9 e 4:5.
- Tela de configurações com seleção de idioma.
- Workflow GitHub Actions para compilar APK debug e publicar artefato.

## Build e correção conhecida
- A primeira compilação do código commitado no repositório falhou em `ExtendedFloatingActionButton`: a chamada no arquivo do repositório estava sem o parâmetro `icon`.
- O ZIP de fonte entregue para a Release contém a chamada com `icon` e `text`, e atualiza `activity-compose` para 1.9.3.
- O novo workflow `.github/workflows/build.yml` baixa `MotionFlow-source.zip` de uma GitHub Release e compila fora da branch `main`.
- O build do ZIP ainda precisa ser executado pelo Actions depois que o arquivo for anexado à Release.

## Limitações conhecidas
- O botão de abrir projeto ainda não navega para o editor.
- Projetos ainda são mantidos apenas em memória; Room e ProjectIO entram na Etapa 2.
- O seletor de idioma altera o estado, mas a tradução integral das telas será concluída na camada i18n.
- Esta etapa ainda não contém canvas, timeline, renderização GL nem exportação.
- O build não foi executado neste ambiente; a compilação deverá ser confirmada pelo GitHub Actions ou ambiente Gradle Android.

## Próxima etapa
Etapa 2: modelo de dados, serialização versionada, persistência, Home funcional, Photo Picker e criação/abertura/salvamento de projetos com imagem.
