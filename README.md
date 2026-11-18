# MotionFlow

Editor Android de motion graphics para celular, feito em Kotlin e Jetpack Compose.

## Versão 0.4.0-alpha

Esta atualização mantém a UI do pacote Claude e incorpora mais padrões observados nos vídeos de referência do Keyflow.

### Referência Keyflow incorporada
- Barra da timeline com grade, modo Full, marcador, navegação quadro/chave anterior e seguinte, reprodução, keyframe, camadas, desfazer e refazer.
- Playhead central verde, régua numerada, marcadores e cabeçalho fixo para visibilidade/tipo da camada.
- Inspetor dividido em **Texto / Style / Layout / Animator**.
- Texto: conteúdo, família tipográfica, peso e tamanho.
- Style: cores de preenchimento, contorno e controle de gradiente.
- Layout: alinhamento, espaçamento, entrelinhas e seção de opções de caminho.
- Animator: cartões Flow/Range, força, atraso e reverso.
- Seleção no canvas com contorno verde e alças; grade de composição liga/desliga.

### O que realmente funciona nesta versão
- Projetos criados entram diretamente no editor, com canvas na proporção escolhida.
- Texto pode ser editado e a família, peso e tamanho refletem na prévia.
- Cor de preenchimento e contorno de texto são renderizados no canvas.
- Alinhamento, espaçamento e entrelinhas são aplicados à tipografia.
- Formas, imagens e primeiro frame de vídeos aparecem na prévia.
- Arrastar, pinçar, girar e ajustar posição/escala no inspetor.
- Visibilidade da camada controla o canvas.
- A régua aceita toque para mover o playhead.
- Play avança o playhead da timeline; botões anterior/próximo navegam entre keyframes ou quadros.
- O botão de keyframe insere um marcador temporal na camada selecionada.
- Marcadores de timeline podem ser inseridos e a grade pode ser ligada/desligada.

### Ainda em desenvolvimento
- Configurações Flow/Range são armazenadas, mas não dirigem animação real ainda.
- Gradient e texto em caminho estão representados na UI; a renderização deles ainda não foi implementada.
- Reprodução sincronizada de vídeo/áudio, waveform e persistência dos projetos.
- Interpolação real de keyframes, efeitos, renderizador de composição e exportação MP4.
- O comando Desfazer/Refazer ainda não cobre todas as mudanças de propriedades.
- Mural, Comunidade e Presets seguem como pontos de extensão.

## Estrutura e build
O código do app fica na branch `source-archive`; a branch `main` contém o workflow. Cada atualização da branch de fonte aciona o GitHub Actions.
Abra [Actions](../../actions) e baixe `MotionFlow-debug-<número>` em **Artifacts** após o build concluir.
