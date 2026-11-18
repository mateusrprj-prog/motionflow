# MotionFlow — Contexto do Projeto

## Objetivo
Construir um editor Android de motion graphics e animação por keyframes, com canvas, timeline, efeitos, áudio/vídeo, recursos 3D, IA local e exportação. O projeto é uma implementação original; não copiar código ou recursos proprietários de outros aplicativos.

## Referência técnica analisada
Pacote fornecido: `Keyflow_1.6.0.apks`, versão indicada pelo nome do arquivo 1.6.0.
A inspeção inicial do bundle identificou:
- `base.apk` e splits para ARM64, português, espanhol e densidade xxhdpi.
- Código DEX em três arquivos no APK base.
- Evidências de Kotlin/Jetpack Compose, MediaPipe, TensorFlow Lite, Rhino JavaScript e OpenGL/GLSL.
- Modelos em `assets/depth/` (`depth_fast.tflite`, `depth_model.tflite`) e `assets/roto/magic_touch.tflite`.
- Referências a keyframes, curvas, expressões, efeitos, rig 3D, bones, morph targets, física e plugins `.kfplug`.

Modelos binários e componentes de terceiros não foram copiados. Licenças, origem e compatibilidade precisam ser verificadas antes de qualquer reutilização.

## Configuração
- Package: `app.motionflow`.
- Kotlin + Jetpack Compose + Material 3.
- minSdk 26, targetSdk 35, compileSdk 35.
- JDK 17, AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9.
- Código-fonte na branch `source-archive`; `main` mantém workflow e documentação.
- Tema escuro e interface desenhada para celular.

## Etapa 1 — Base funcional
- Estrutura Gradle Kotlin DSL e version catalog.
- Activity única com Jetpack Compose.
- Home inicial, criação de projetos com proporções 9:16, 1:1, 16:9 e 4:5.
- Configurações de idioma.
- ViewModel com lista de projetos em memória.
- GitHub Actions compilando APK debug.
- Build de referência anterior confirmado com sucesso na execução 16 do Actions.

## Etapa 2 — UI 0.2.0-alpha
Pacote de UI criado para o projeto e integrado:
- Tela inicial renovada com Mural, categorias/modelos, projetos recentes, novidades e barra inferior.
- A lista de projetos agora usa os dados do ViewModel e abre o editor.
- Editor com barra superior, área de prévia, controles de transporte, timeline horizontal, trilhas, seleção/visibilidade de camadas, inspetor e painel de curvas.
- Ações de adicionar forma, texto, ajuste e câmera 3D.
- Importação de imagens/vídeos e áudio com o seletor nativo do Android.
- Desfazer/refazer básico para inclusão de camadas.
- Versão atualizada para `0.2.0-alpha`, versionCode 2.

## Limitações da etapa 2
- A área de prévia é um placeholder; ainda não existe renderizador para desenhar as mídias importadas.
- URIs e camadas permanecem em memória e não sobrevivem ao encerramento do app.
- O botão de reprodução ainda não executa o playback real.
- Keyframes e valores do inspetor ainda são interface demonstrativa; não alteram um motor de animação.
- Curvas ainda não estão ligadas ao sistema de interpolação.
- Exportação ainda não foi implementada.
- As abas Comunidade/Projetos/Presets e o Mural exibem avisos de desenvolvimento.
- Build da versão 0.2.0 precisa ser validado pelo Actions após a integração.

## Próxima etapa sugerida
Implementar modelo de documento versionado e persistência, armazenamento das mídias importadas, canvas 2D real e sistema básico de composição. Em seguida, ligar timeline, propriedades e keyframes a um modelo de animação real antes de adicionar exportação.
