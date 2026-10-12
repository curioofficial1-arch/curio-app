package com.example.data.sample

import com.example.data.model.CurioItem

object InitialCuriosities {
    val sampleList = listOf(
        CurioItem(
            id = "curio_brain_dying",
            title = "What happens to the brain when a person is dying",
            category = "Brain & Mind",
            spark = "I was watching a video about near-death experiences and wondered what doctors actually see happening inside the brain right when someone passes away.",
            overview = "Scientists discovered that when a person's heart stops, the brain doesn't just switch off like a light bulb. For up to several minutes, there is a sudden, organized burst of brainwaves—the exact same high-energy waves we have when remembering our dearest memories, dreaming, or concentrating deeply. Hearing is the last sense to fade, and the brain releases natural calming chemicals that explain why people feel peace instead of fear.",
            keyTakeaways = listOf(
                "A sudden burst of memory brainwaves: Monitors show a surge of brain activity for several minutes after the heart stops, matching vivid memory recall.",
                "Hearing stays awake the longest: Tests show the brain still responds to familiar voices even when someone is fully unresponsive.",
                "Natural calming chemicals flood the body: The brain releases feel-good hormones that relieve pain and fear, creating peaceful detachment.",
                "Last moments of clarity: People with severe memory loss or confusion often unexpectedly become alert and speak clearly right before passing away."
            ),
            tangents = listOf(
                "Why do people report seeing a bright light or feeling out of body?",
                "Can cooling the body down give doctors more time to save someone?",
                "Why does hearing outlast sight and touch?"
            ),
            sources = listOf(
                "NYU Langone Medical Study on Resuscitation and Awareness",
                "Proceedings of the National Academy of Sciences (Dying Brain Activity Report)",
                "Frontiers in Aging Neuroscience Journal"
            ),
            rabbitHoleDepth = 4,
            status = "Solved",
            isFavorite = true,
            coverDrawable = "hero_brain_dying",
            colorHex = "#6366F1",
            readTimeMinutes = 4
        ),
        CurioItem(
            id = "curio_mancity_downfall",
            title = "The man behind the Manchester City investigation",
            category = "Sports & Scandals",
            spark = "I kept seeing headlines about Manchester City facing 115 financial rule charges and wanted to find out who actually uncovered all the hidden evidence.",
            overview = "It wasn't rival teams or the football league that caught Manchester City. It was a computer programmer from Portugal named Rui Pinto. Working from an apartment in Budapest under the pseudonym 'John', he hacked and leaked millions of confidential emails. The emails showed the club allegedly used secret offshore accounts and inflated sponsor deals to inject owner money while dodging financial fair play spending rules.",
            keyTakeaways = listOf(
                "The Football Leaks whistleblower: Rui Pinto leaked over 70 million private club documents to German news reporters.",
                "Disguised sponsor money: Leaked emails showed official sponsors paid only a small part of their bills, with club owners secretly paying the rest.",
                "Secret second paychecks: Managers were paid an official salary in England and an extra tax-free salary through offshore consulting deals.",
                "The 115 Premier League charges: While UEFA's earlier ban was reduced due to a 5-year time limit, the Premier League has no expiration limit on investigating past fraud."
            ),
            tangents = listOf(
                "What punishments could Manchester City face if found guilty?",
                "How did the Portuguese hacker avoid prison time?",
                "How do financial fair play rules work for billionaire owners?"
            ),
            sources = listOf(
                "Der Spiegel Football Leaks Investigative Series",
                "Court of Arbitration for Sport (CAS) Official Ruling",
                "The Guardian: The Hacker Who Shook European Football"
            ),
            rabbitHoleDepth = 4,
            status = "Active",
            isFavorite = true,
            coverDrawable = "hero_mancity_investigation",
            colorHex = "#0284C7",
            readTimeMinutes = 5
        ),
        CurioItem(
            id = "curio_bronze_age",
            title = "Why civilization vanished in 1177 BC",
            category = "History",
            spark = "How did the world's most powerful ancient empires all crumble together in just fifty years?",
            overview = "Around 1177 BC, the major powers of the Mediterranean (Greeks, Egyptians, Hittites, and Babylonians) were thriving and trading with each other. Then, almost all of them collapsed at once. Historians discovered it wasn't just mysterious raiders called the Sea Peoples—it was a chain reaction of mega-droughts, crop failures, and broken trade routes that caused the entire ancient global economy to snap.",
            keyTakeaways = listOf(
                "Everything was connected: Just like today, ancient empires relied on global supply chains—especially tin from thousands of miles away to make bronze tools.",
                "Mega-droughts caused mass famine: Pollen samples prove severe droughts ruined crops across the region for decades.",
                "Desperate raiders stormed the coasts: Starving populations formed raiding fleets that attacked cities looking for food.",
                "A four-hundred-year dark age: After the palaces burned, writing and complex government vanished for centuries until classical Greece arose."
            ),
            tangents = listOf(
                "Who were the mysterious Sea Peoples depicted on temple walls?",
                "How did the discovery of iron change warfare after bronze collapsed?"
            ),
            sources = listOf(
                "1177 B.C.: The Year Civilization Collapsed by Eric Cline",
                "Nature Communications Climate Study"
            ),
            rabbitHoleDepth = 3,
            status = "Solved",
            isFavorite = false,
            coverDrawable = "hero_curio_cabinet",
            colorHex = "#F59E0B",
            readTimeMinutes = 4
        ),
        CurioItem(
            id = "curio_voynich",
            title = "The 600-year-old book nobody can read",
            category = "Mysteries",
            spark = "I saw photos of an illustrated medieval book written in a completely unknown alphabet that no codebreaker on Earth has ever solved.",
            overview = "Made in the early 1400s, the Voynich Manuscript contains 240 illustrated pages written in a mysterious fluid handwriting with drawings of plants that don't exist on Earth, strange star charts, and bathing figures. Even Alan Turing's World War II codebreakers and modern supercomputers couldn't decode a single word, leaving historians debating whether it is a lost secret science book or an elaborate medieval prank.",
            keyTakeaways = listOf(
                "Real language patterns: Word counts in the text follow the mathematical rules of real human languages, meaning it isn't random typing.",
                "Made from genuine medieval calfskin: Carbon dating confirmed the parchment was made between 1404 and 1438.",
                "Plants that don't exist: Over a hundred plant drawings have puzzled botanists because almost none match living plant species.",
                "Defeated top codebreakers: The finest military cryptographers with supercomputers have failed to read a single sentence."
            ),
            tangents = listOf(
                "Could the manuscript be written in an artificial made-up language?",
                "What did modern chemical ink tests reveal about who made it?"
            ),
            sources = listOf(
                "Yale University Rare Book & Manuscript Library",
                "PLOS ONE Computational Linguistics Analysis"
            ),
            rabbitHoleDepth = 3,
            status = "Active",
            isFavorite = false,
            coverDrawable = null,
            colorHex = "#EC4899",
            readTimeMinutes = 3
        )
    )

    val curiositySparks = listOf(
        InspirationPrompt(
            topic = "Cicada 3301: The Internet's Most Famous Secret Hunt",
            category = "Mysteries",
            spark = "In 2012, an anonymous post invited highly smart people to solve clues hidden inside pictures and GPS coordinates across nine countries.",
            suggestedColor = "#EC4899"
        ),
        InspirationPrompt(
            topic = "Why We Sleep: The Nighttime Brain Cleaning Cycle",
            category = "Brain & Mind",
            spark = "While you are asleep, brain cells shrink slightly to let spinal fluid wash through and clean out waste that builds up during the day.",
            suggestedColor = "#6366F1"
        ),
        InspirationPrompt(
            topic = "Formula 1 Aerodynamics: How Air Beat Engine Power",
            category = "Sports & Scandals",
            spark = "How race car designer Adrian Newey used simple pencil sketches to shape upside-down wings that sucked cars to the track.",
            suggestedColor = "#0284C7"
        ),
        InspirationPrompt(
            topic = "The Fermi Paradox: Where Is Everybody in the Universe?",
            category = "Science",
            spark = "With billions of stars and planets, why haven't we heard from aliens? Could civilizations be hiding from each other?",
            suggestedColor = "#10B981"
        )
    )
}

data class InspirationPrompt(
    val topic: String,
    val category: String,
    val spark: String,
    val suggestedColor: String
)
