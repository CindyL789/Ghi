package com.example.data.model

data class BeginnerPattern(
    val title: String,
    val description: String,
    val stitchesUsed: List<String>,
    val estimatedTime: String,
    val patternUrl: String,
    val videoTutorialUrl: String = "",
    val category: String = "Wearables"
)

object BeginnerPatternsData {
    val samplePatterns = listOf(
        BeginnerPattern(
            title = "1. Classic Beginner Granny Square",
            description = "The absolute quintessential beginner project. Learn to work in rounds, create corners, and join colorful clusters together.",
            stitchesUsed = listOf("Chain (ch)", "Double Crochet (dc)", "Slip Stitch (sl st)"),
            estimatedTime = "30 mins per square",
            patternUrl = "https://www.sarahmaker.com/how-to-crochet-a-granny-square/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=np-Q8WbY9Wk",
            category = "Blankets & Squares"
        ),
        BeginnerPattern(
            title = "2. Easy 1.5-Hour Ribbed Beanie",
            description = "Crochet a flat rectangle using half double crochets in the back loop, then seam into a trendy, stretchy winter beanie with a faux fur pom.",
            stitchesUsed = listOf("Chain (ch)", "Half Double Crochet (hdc)", "Back Loop Only (BLO)"),
            estimatedTime = "1.5 - 2 hours",
            patternUrl = "https://makeanddocrew.com/easy-crochet-beanie-pattern-free/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=kYv9G_q_mYg",
            category = "Wearables"
        ),
        BeginnerPattern(
            title = "3. Farmhouse Textured Dishcloth & Washcloth",
            description = "A fast, functional, and forgiving project using cotton yarn. Great for practicing consistent tension and clean edge turns.",
            stitchesUsed = listOf("Single Crochet (sc)", "Double Crochet (dc)"),
            estimatedTime = "45 mins",
            patternUrl = "https://midwesternmoms.com/crochet-dishcloth-pattern/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=78pT5rP17rA",
            category = "Home & Kitchen"
        ),
        BeginnerPattern(
            title = "4. Chunky Cloud Infinity Scarf",
            description = "Using super bulky yarn and a large 9mm hook, this cozy scarf works up fast and looks luxurious with minimal stitch complexity.",
            stitchesUsed = listOf("Foundation Chain", "Double Crochet (dc)", "Slip Stitch Join"),
            estimatedTime = "2 hours",
            patternUrl = "https://www.leeleeknits.com/easy-chunky-crochet-infinity-scarf-pattern/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=5xKSS8pWp14",
            category = "Wearables"
        ),
        BeginnerPattern(
            title = "5. Modern Boho Cotton Coaster Set",
            description = "Quick circular coasters with optional cute fringe edges. Perfect for mastering the Magic Ring and working flat circles.",
            stitchesUsed = listOf("Magic Ring (MR)", "Single Crochet (sc)", "Slip Stitch (sl st)"),
            estimatedTime = "20 mins each",
            patternUrl = "https://sarahmaker.com/crochet-coasters-pattern/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=p298Hxyn5O8",
            category = "Home & Kitchen"
        ),
        BeginnerPattern(
            title = "6. Easy Beginner Amigurumi Mini Whale",
            description = "The friendliest introduction to 3D Amigurumi crochet with seamless rounds, basic increases/decreases, and safety eyes.",
            stitchesUsed = listOf("Magic Ring (MR)", "Single Crochet (sc)", "Increase (inc)", "Invisible Decrease (dec)"),
            estimatedTime = "1 hour",
            patternUrl = "https://www.stitchbyfay.com/crochet-whale-pattern/",
            videoTutorialUrl = "https://www.youtube.com/watch?v=33K16A_eL_U",
            category = "Amigurumi"
        )
    )
}
