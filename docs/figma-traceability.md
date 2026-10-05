# Rastreabilidade do protótipo Marvel

Layouts implementados a partir da página **Protótipo** do arquivo Figma **Marvel - Mobile**
(frame base 412×917, fonte Poppins). Cada layout XML tem um comentário no topo com o `node-id`
do Figma e as medidas usadas.

## Mapa Figma → layout

| Frame Figma (node) | Layout | Fragment |
|---|---|---|
| SplashScreen `13:133` | `fragment_splash.xml` | `SplashFragment` |
| Onboarding `41:980` / `41:996` / `41:1010` | `fragment_onboarding.xml` + `item_onboarding.xml` | `OnboardingFragment` |
| Home `12:121` | `fragment_home.xml` + `item_banner.xml` + `item_hero_circle.xml` + `item_team_card.xml` | `HomeFragment` |
| Home (equipe) `31:400` | `fragment_team.xml` + `include_info_card.xml` + `item_comic.xml` | `TeamFragment` |
| Home (HQ´s) `41:886` | `fragment_comics.xml` + `item_comic_grid.xml` | `ComicsFragment` |
| Busca `21:1387` (dark) / `24:49` (light) | `fragment_search.xml` + `item_character_result.xml` | `SearchFragment` |
| Busca (detalhe) `25:174` | `fragment_character_detail.xml` + `item_power_chip.xml` | `CharacterDetailFragment` |
| Personagem Details `8:14` / `9:38` / `10:58` | `fragment_team_members.xml` + `item_member.xml` | `TeamMembersFragment` |
| Linha do tempo `33:531` | `fragment_timeline.xml` + `item_timeline.xml` | `TimelineFragment` |
| Linha do tempo details `36:721` | `fragment_timeline_detail.xml` | `TimelineDetailFragment` |

Componentes compartilhados: `include_toolbar.xml` (logo ou voltar+título, toggle sol/lua) e
`include_bottom_nav.xml` (3 abas, indicador 10×6 sob a ativa).

## Tokens

- **Cores de marca** (`values/colors.xml`, iguais nos dois temas): `#A90C08` vermelho, `#DBBA15` amarelo,
  `#022D99` azul, `#011F69` azul escuro, `#518CCA` azul claro, `#2FAA02` verde, `#E23636` dot ativo.
- **Tema escuro** (`values-night/colors.xml`): fundo `#1E1E1E`, superfície `#262626`, nav `#191919`,
  texto `#FFFFFF` / `#A8A8A8`, ícone inativo `#4C4C4C`.
- **Tema claro** (`values/colors.xml`): fundo `#FFFFFF`, superfície `#E7E7E7`, busca `#E6E6E6`,
  nav `#EEEEEE`, texto `#2B2B2B`, ícone inativo `#A8A8A8`.
- **Tipografia** (`values/styles.xml`, `Text.Marvel.*`): Poppins 300/400/500/600 nos tamanhos do Figma
  (50, 40, 34, 28, 26, 24, 22, 20, 18, 16, 15, 14, 13, 12, 11, 9).
- O toggle de tema persiste em `SharedPreferences` (`ThemeManager`) e aplica via `AppCompatDelegate`.

## Assets

`drawable-nodpi/img_*.png` são recortes dos frames do Figma (2x) usados como placeholders fiéis
até a integração com a API. Artes de personagem "sem fundo" (bolinhas da Home e carrossel de membros)
tiveram o fundo `#1E1E1E` removido para funcionar no tema claro.

## Fora desta etapa

- Dados reais: `MockData` continua como fonte; a Comic Vine API entra na próxima fase.
- Filtro de época e botão de sliders na linha do tempo são visuais (sem ação).
