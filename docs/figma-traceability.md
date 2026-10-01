# Rastreabilidade do protótipo Marvel

Implementação inicial baseada somente na página **Protótipo** do arquivo Figma **Marvel - Mobile**.

- Escopo visual: variante escura para telefone em orientação retrato.
- Fluxo: splash, onboarding, home, busca, detalhes do personagem, equipe, membros, HQs, linha do tempo e detalhe do arco.
- Home: carrossel com avanço automático a cada 4,5 segundos e navegação manual por gesto.
- Dados: conteúdo local mockado em `MockData`; nenhum endpoint é chamado nesta etapa.
- Imagens: representações locais por iniciais e blocos de cor para manter a compilação independente de rede. A troca por artes finais fica isolada nos layouts e adaptadores.
- Tema claro: mantido fora desta etapa conforme a definição do projeto.
