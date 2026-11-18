# MotionFlow — Contexto do Projeto

## Objetivo
Construir um editor Android original de motion graphics/animação, com canvas, timeline, keyframes, mídia, áudio/vídeo, efeitos, 3D e exportação. A UI deve manter o layout mobile do pacote Claude e incorporar padrões de fluxo observados nas referências do Keyflow.

## Configuração
- Package: `app.motionflow`.
- Kotlin + Jetpack Compose + Material 3.
- minSdk 26, targetSdk 35, compileSdk 35.
- JDK 17, AGP 8.7.3, Kotlin 2.0.21, Gradle 8.9.
- Código do app em `source-archive`; `main` mantém workflow e documentação.
- Versão atual: `0.4.0-alpha`, versionCode 4.

## Fontes de referência
- Pacote de interface MotionFlow enviado pelo usuário: base visual da Home, editor, timeline e docas.
- Três gravações recentes de referência do Keyflow:
  - timeline com régua numerada, playhead verde, trilhas, toolbar de ações, botões de play, navegação, marcadores e painéis.
  - editor de texto com abas Text/Style/Layout/Animator.
  - Style com Fill, paleta de cores, Stroke e opção Gradient.
  - Layout com espaçamento, line height e opções de caminho.
  - Animator com Flow/Range, Strength, Delay e propriedades de transformação.
- Captura de tela atual do MotionFlow: editor com canvas claro, alças de seleção e inspetor em painel inferior.

O APK original do Keyflow não está anexado nesta rodada nem apareceu na Biblioteca de arquivos. Esta atualização foi construída a partir do que é visível nas capturas e gravações fornecidas, além do contexto técnico já analisado anteriormente. MotionFlow é implementação independente; não inclui código-fonte, modelos ou assets proprietários do Keyflow.

## UI do Claude preservada
- Home com Mural, categorias/modelos, projetos recentes, novidades e navegação inferior.
- Editor com barra superior, área de prévia, transporte, timeline e dock Forma/Mídia/Áudio/Texto/Elemento.
- Identidade escura rosa/lilás em `ui/kit/UiTokens.kt`.
- Verde Keyflow usado apenas em estados ativos do editor, seleção e playhead.

## Etapa 1 — Base
- Estrutura Gradle, tema e Activity Compose.
- ViewModel com criação e lista de projetos em memória.
- Home e diálogo com proporções 9:16, 1:1, 16:9 e 4:5.
- Workflow Actions para APK debug.

## Etapa 2 — UI 0.2.0-alpha
- Home atualizada com Mural, modelos, projetos recentes e navegação.
- Abertura de projeto na tela do editor.
- Timeline, dock, inspetor e painel de curvas.

## Etapa 3 — Canvas 2D 0.3.0-alpha
- Criar projeto abre diretamente o Editor.
- Canvas adapta artboard à proporção do projeto.
- Camadas de forma e texto; importação de imagem, vídeo e áudio.
- Imagens e primeiro frame de vídeo aparecem na prévia.
- Elementos selecionáveis, arrastáveis, escaláveis e rotacionáveis.
- Posição, escala e rotação pelo inspetor.
- Visibilidade sincronizada com a prévia.

## Etapa 4 — Fluxo de edição inspirado no Keyflow 0.4.0-alpha
- Timeline: barra de ações, régua, playhead verde, marcadores, cabeçalho fixo de camada.
- Botões de navegação entre keyframes/quadros; play avança o playhead.
- Keyframe temporal pode ser adicionado à camada selecionada.
- Inspetor com abas Texto, Style, Layout e Animator.
- Texto: conteúdo, família, peso e tamanho.
- Style: preenchimento, paleta, stroke e gradiente armazenados.
- Layout: alinhamento, letter spacing e line height.
- Animator: seleção Flow/Range, strength, delay e reverse guardados no modelo.
- Canvas: grade de composição, seleção verde e alças.

## Limitações atuais
- As configurações Flow/Range ainda não conduzem movimento durante playback.
- Os keyframes inserem tempos na camada, mas ainda não armazenam snapshots de valores e não interpolam transformações.
- Gradiente e texto em caminho são UI/configuração, não renderizados ainda.
- O playback move o playhead, mas não sincroniza os itens de vídeo e áudio.
- Estado das camadas ainda é mantido em memória; falta persistência.
- Desfazer/Refazer cobre inclusão de camadas, não todas as alterações.
- Renderizador de composição e exportação MP4 ainda não existem.
- Mural, Comunidade e biblioteca de Presets seguem como pontos de extensão.

## Próxima etapa
1. Histórico de comandos (undo/redo) para transformação e estilo.
2. Keyframes reais com snapshots de posição, escala, rotação, opacidade e interpolação por curvas.
3. Persistência versionada dos projetos e cópia segura/importação de mídia.
4. Reprodução de vídeo/áudio sincronizada.
5. Efeitos e renderização/exportação MP4.
