# MotionFlow

Editor Android de motion graphics para celular, construído com Kotlin e Jetpack Compose.

## Versão 0.3.0-alpha
Esta versão evolui a UI do pacote `MotionFlow-UI.zip` para um canvas 2D interativo.

### Interface preservada do pacote Claude
- Home com Mural, categorias/modelos, projetos recentes, novidades e barra inferior.
- Editor com barra superior, prévia, transporte, timeline e dock Forma/Mídia/Áudio/Texto/Elemento.
- Inspetor de propriedades e painel de curvas mantendo o mesmo estilo escuro e acentos rosa/lilás.

### Canvas 2D adicionado
- Projetos criados abrem diretamente no editor.
- Artboard ajustado à proporção do projeto (vertical, quadrado ou horizontal).
- Adição de formas e textos.
- Edição do conteúdo das camadas de texto pelo inspetor.
- Importação de imagens e vídeo pelo seletor Android; imagens e primeiro frame do vídeo são mostrados na prévia.
- Arrastar, pinçar para redimensionar e girar elementos no canvas.
- Posição, escala e rotação editáveis no inspetor.
- Camadas visíveis/ocultas sincronizadas com a prévia.
- Camadas de áudio listadas na timeline.

## Ainda em desenvolvimento
- Persistência de projetos e URIs após fechar o app.
- Decodificação de todos os formatos de imagem/vídeo e visualização de áudio/waveform.
- Playback real ligado à timeline.
- Sistema de keyframes e interpolação ligado aos parâmetros do canvas.
- Editor vetorial avançado, efeitos e renderização/exportação de vídeo.
- Mural, Comunidade e biblioteca de Presets.

## Compilar
O código-fonte fica em `source-archive`; `main` mantém o workflow. Atualizações dessa branch acionam o GitHub Actions.
Abra [Actions](../../actions), selecione a execução mais recente e baixe `MotionFlow-debug-<número>` em **Artifacts** quando o build concluir.
