import {defineConfig} from 'vitepress'

// https://vitepress.vuejs.org/config/app-configs
export default defineConfig({
    head: [
        ['link', {rel: 'icon', type: 'image/svg+xml', href: '/logo.svg'}],
        ['meta', {name: 'keywords', content: 'SessionAPI, Minecraft, game, containerization, server, minigames'}],
        ['meta', {name: 'author', content: 'Kamil Szelągiewicz'}],
        ['meta', {property: 'og:title', content: 'SessionAPI Documentation'}],
        ['meta', {property: 'og:description', content: 'Library for game containerization on Minecraft servers.'}],
        ['meta', {property: 'og:image', content: '/img/logo.svg'}]
    ],
    title: 'SessionAPI',
    description: 'The library facilitates game containerization on a Minecraft server, enabling the creation of isolated environments within the game with separate logic and state.',
    outDir: "../../docs",
    base: "/SessionAPI/",

    locales: {
        root: {
            label: 'Polski',
            lang: 'pl',
        },
        en: {
            label: "English",
            lang: 'en',
        }
    },

    themeConfig: {
        logo: '/logo.svg',
        nav: [
            {text: 'Community', link: 'https://discord.gg/za2pYfGWRN'},
            {text: 'GitHub', link: 'https://github.com/Szelagi/SessionAPI'},
        ],

        socialLinks: [
            {icon: 'discord', link: 'https://discord.gg/za2pYfGWRN'},
            {icon: 'linkedin', link: 'https://www.linkedin.com/in/kamil-szelagiewicz/'},
            {icon: 'github', link: 'https://github.com/Szelagi/SessionAPI'}
        ],

        sidebar: {
            pl: [
                {
                    text: 'Wprowadzenie',
                    items: [
                        {text: 'Korzyści i rozwiązania', link: '/pl/introduction/key-features.md'},
                        {text: 'Zastosowania', link: '/pl/introduction/application.md'},
                        {text: 'Modularne podejście', link: '/pl/introduction/modularity.md'},
                        {text: 'Pierwszy projekt', link: '/pl/introduction/first-project.md'}
                    ]
                },
                {
                    text: 'Przewodnik techniczny',
                    items: [
                        {text: 'Instalacja', link: '/pl/technical/installation.md'},
                        {text: 'Zdarzenia wewnętrzne', link: '/pl/technical/internal-events.md'},
                        {text: 'Kolejność zdarzeń lokalna', link: '/pl/technical/sequence-event-execution.md'},
                        {text: 'Drzewa zagnieżdżone', link: '/pl/technical/nested-trees.md'},
                        {text: 'Kreator map', link: '/pl/technical/creator.md'},
                        {text: 'Zdarzenia bukkit', link: '/pl/technical/listeners.md'},
                        {text: 'Zadania i wątki', link: '/pl/technical/tasks.md'},
                        {text: 'Kontenery graczy', link: '/pl/technical/container.md'},
                        {text: 'Przywracanie stanu', link: '/pl/technical/recovery.md'},
                        {text: 'Generowanie mapy', link: '/pl/technical/board.md'},
                        {text: 'Własne zdarzenia', link: '/pl/technical/custom-events.md'},
                    ]
                },
                {
                    text: 'Wbudowane komponenty',
                    items: [
                        {text: 'Algorytmy grupujące', link: '/pl/buildin/grouping.md'},
                        {text: 'Osobny ekwipunek', link: '/pl/buildin/other-equipment.md'},
                        {text: 'Osobny tryb gry', link: '/pl/buildin/other-gamemode.md'},
                        {text: 'Interaktywny sklep', link: '/pl/buildin/trader.md'},
                        {text: 'Generator itemów', link: '/pl/buildin/generator.md'},
                        {text: 'Poczekalnia', link: '/pl/buildin/lobby.md'},
                    ]
                },
                {
                    text: 'Przykładowe projekty',
                    items: [
                        {text: 'Gra CakeWars', link: '/pl/project/cakewars.md'},
                    ]
                },
                {
                    text: 'Artykuły',
                    items: [
                        {text: 'Migracja do v2.3', link: '/pl/migration_2.3'},
                        {text: 'Bezpieczeństwo w grach', link: '/pl/security'}
                    ]
                },
            ],
            en: [
                {
                    text: 'Basics',
                    items: [
                        {text: 'Introduction', link: '/en/'},
                    ]
                },
                {
                    text: 'Articles',
                    items: [
                        {text: 'Migration to 2.3', link: '/en/migration_2.3'},
                    ]
                },
            ]
        },

        localeLinks: {
            text: 'Language',
            items: [
                { text: 'Polski', link: '/pl/' },
                { text: 'English', link: '/en/' }
            ]
        }
    },

});
