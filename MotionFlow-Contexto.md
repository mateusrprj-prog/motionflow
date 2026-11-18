# MotionFlow — Contexto do Projeto

## Objetivo
Construir um editor Android original de motion graphics/animação, com canvas 2D, timeline, keyframes, mídia, áudio/vídeo, recursos 3D e exportação. A interface deve manter o desenho mobile do pacote UI enviado pelo usuário.

## Configuração
- Package: `app.motionflow`.
- Kotlin + Jetpack Compose + Material 3.
- minSdk 26, targetSdk 35, compileSdk 35.
- JDK 17, AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9.
- Código do aplicativo em `source-archive`; `main` mantém workflow e documentação.

## Referência técnica
Foi inspecionado o pacote fornecido pelo usuário `Keyflow_1.6.0.apks`. O projeto MotionFlow é uma implementação independente; não incorpora código, modelos binários ou recursos proprietários do aplicativo de referência. Verificar licenças de dependências antes de adicionar bibliotecas ou modelos externos.

## UI do Claude integrada
O pacote `MotionFlow-UI.zip` é tratado como fonte visual:
- Home: Mural, modelos/categorias, projetos recentes, novidades, navegação inferior.
- Editor: barra superior, prévia 4:3, controles de transporte, timeline, dock Forma/Mídia/Áudio/Texto/Elemento.
- Timeline: agulha central, régua horizontal, cabeçalho de camada, clipes e marcadores.
- Painéis: inspetor e curvas/easing.
As cores rosa/lilás e o tema escuro ficam em `ui/kit/UiTokens.kt`.

## Etapa 1 — Base
- Estrutura Gradle, tema e Activity Compose.
- ViewModel com criação e lista de projetos em memória.
- Home e diálogo com proporções 9:16, 1:1, 16:9 e 4:5.
- Workflow Actions para compilar APK debug.

## Etapa 2 — UI 0.2.0-alpha
- Nova Home com a composição visual do pacote Claude.
- Projetos reais do ViewModel mapeados para a lista.
- Navegação da Home para o Editor.
- Timeline, dock, inspetor e painel de curvas.

## Etapa 3 — Canvas 2D 0.3.0-alpha
- Criar projeto abre imediatamente o Editor.
- Canvas adapta o artboard à proporção do projeto.
- Camadas de forma e texto podem ser adicionadas.
- Conteúdo das camadas de texto pode ser editado no inspetor.
- Importação de imagens e vídeos com o seletor nativo Android.
- Imagens e primeiro frame de vídeo aparecem na prévia.
- Elementos podem ser selecionados, arrastados, escalados e girados com gestos.
- Posição, escala e rotação podem ser ajustadas pelo inspetor.
- Visibilidade da camada controla a prévia.
- Áudio pode ser importado e aparece como trilha na timeline.
- Desfazer/refazer simples de inclusão de camadas.

## Limitações atuais
- Estado de projeto e camadas ainda está apenas em memória; falta persistência serializada.
- Vídeo está representado pelo primeiro frame; ainda não há decodificação contínua.
- Áudio é representado na timeline, sem waveform nem playback.
- O botão Play ainda não dirige uma linha do tempo de composição.
- Os losangos são UI; keyframes ainda não estão ligados à interpolação de posição/escala/rotação.
- O motor de renderização e exportação de vídeo ainda não existe.
- Efeitos, Mural, Comunidade e Presets permanecem pontos de extensão visuais.
- A compilação da 0.3.0 deve ser validada pelo GitHub Actions antes de distribuir APK.

## Próximas etapas
1. Persistência do projeto e cópia/importação controlada de mídias.
2. Reprodução e sincronização temporal para imagens, vídeo e áudio.
3. Keyframes reais para transformações e curvas de interpolação.
4. Renderização e exportação MP4.
