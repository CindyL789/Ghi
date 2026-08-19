package com.example.data.model

data class GlossaryTerm(
    val usTerm: String,
    val ukTerm: String,
    val abbreviation: String,
    val description: String,
    val category: String = "Basic Stitches",
    val difficulty: String = "Beginner",
    val videoUrl: String = "",
    val tips: String = ""
)

object CrochetGlossaryData {
    val sampleTerms = listOf(
        GlossaryTerm(
            usTerm = "Single Crochet",
            ukTerm = "Double Crochet",
            abbreviation = "sc",
            description = "Insert hook into stitch, yarn over and pull up loop, yarn over and pull through both loops on hook.",
            category = "Basic Stitches",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=1K_uGeqp460",
            tips = "Keep tension relaxed so inserting into the loops remains smooth and easy."
        ),
        GlossaryTerm(
            usTerm = "Half Double Crochet",
            ukTerm = "Half Treble Crochet",
            abbreviation = "hdc",
            description = "Yarn over, insert hook into stitch, yarn over and pull up loop, yarn over and pull through all three loops on hook.",
            category = "Basic Stitches",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=78pT5rP17rA",
            tips = "A quick, squishy stitch perfect for beanies, scarves, and cozy blankets."
        ),
        GlossaryTerm(
            usTerm = "Double Crochet",
            ukTerm = "Treble Crochet",
            abbreviation = "dc",
            description = "Yarn over, insert hook into stitch, yarn over and pull up loop, [yarn over and pull through 2 loops] twice.",
            category = "Basic Stitches",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=5xKSS8pWp14",
            tips = "Twice the height of single crochet. Great for classic granny squares and sweaters."
        ),
        GlossaryTerm(
            usTerm = "Treble Crochet",
            ukTerm = "Double Treble Crochet",
            abbreviation = "tr",
            description = "Yarn over twice, insert hook into stitch, pull up loop, [yarn over and pull through 2 loops] 3 times.",
            category = "Basic Stitches",
            difficulty = "Easy",
            videoUrl = "https://www.youtube.com/watch?v=Nn1EaB9Yv1U",
            tips = "Creates an open, drapey lace texture with taller vertical posts."
        ),
        GlossaryTerm(
            usTerm = "Slip Stitch",
            ukTerm = "Slip Stitch",
            abbreviation = "sl st",
            description = "Insert hook into stitch, yarn over and pull directly through loop on hook. Used to join rounds or create flat edges.",
            category = "Basic Stitches",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=AFk-Cj9aB_Y",
            tips = "Don't pull slip stitches too tight when joining rounds or it can warp the seam."
        ),
        GlossaryTerm(
            usTerm = "Magic Ring / Adjustable Loop",
            ukTerm = "Magic Circle",
            abbreviation = "MR / MC",
            description = "Loop yarn around fingers to create an adjustable center ring for working in the round with zero hole in the center.",
            category = "Techniques",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=p298Hxyn5O8",
            tips = "Essential foundation for Amigurumi and circular mandalas. Weave tail in both directions to lock."
        ),
        GlossaryTerm(
            usTerm = "Granny Square Stitch",
            ukTerm = "Granny Cluster",
            abbreviation = "3-dc cluster",
            description = "Groups of 3 double crochets worked into the same chain space, separated by chain stitches.",
            category = "Classic Patterns",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=np-Q8WbY9Wk",
            tips = "The timeless classic. Perfect for turning scrap yarn into vibrant blankets and cardigans."
        ),
        GlossaryTerm(
            usTerm = "Moss Stitch / Linen Stitch",
            ukTerm = "Linen Stitch",
            abbreviation = "sc, ch 1",
            description = "Alternate (single crochet, chain 1), then work single crochets into the chain-1 spaces of previous row.",
            category = "Texture Stitches",
            difficulty = "Beginner",
            videoUrl = "https://www.youtube.com/watch?v=d_kF1n9RkI4",
            tips = "Produces a gorgeous woven, non-stretchy fabric with clean straight edges."
        ),
        GlossaryTerm(
            usTerm = "Front Post Double Crochet",
            ukTerm = "Front Post Treble",
            abbreviation = "FPdc",
            description = "Work double crochet around the vertical post of the stitch below from front to back to front.",
            category = "Texture Stitches",
            difficulty = "Intermediate",
            videoUrl = "https://www.youtube.com/watch?v=ZfWc5R7iR4U",
            tips = "Raised stitch that creates dramatic cables, waffle patterns, and ribbed brim hats."
        ),
        GlossaryTerm(
            usTerm = "Back Post Double Crochet",
            ukTerm = "Back Post Treble",
            abbreviation = "BPdc",
            description = "Work double crochet around post of stitch from back to front to back. Combined with FPdc for stretchy ribbing.",
            category = "Texture Stitches",
            difficulty = "Intermediate",
            videoUrl = "https://www.youtube.com/watch?v=ZfWc5R7iR4U",
            tips = "Work slowly and use hook tip to guide through the back post cleanly."
        ),
        GlossaryTerm(
            usTerm = "Bobble Stitch",
            ukTerm = "Bobble Stitch",
            abbreviation = "bo",
            description = "Work 4-5 unfinished dc stitches into the same stitch, then yarn over and pull through all loops on hook.",
            category = "Special Stitches",
            difficulty = "Intermediate",
            videoUrl = "https://www.youtube.com/watch?v=E-b0nNq3qHQ",
            tips = "Push the bobble bubble towards the right side of work when closing."
        ),
        GlossaryTerm(
            usTerm = "Popcorn Stitch",
            ukTerm = "Popcorn Stitch",
            abbreviation = "pop",
            description = "Work 5 full dc in stitch, drop loop from hook, insert hook into 1st dc, grab dropped loop and pull through.",
            category = "Special Stitches",
            difficulty = "Intermediate",
            videoUrl = "https://www.youtube.com/watch?v=LqUu-sWwOio",
            tips = "Creates a firmer, rounder 3D pop compared to bobbles or clusters."
        ),
        GlossaryTerm(
            usTerm = "Waffle Stitch",
            ukTerm = "Waffle Stitch",
            abbreviation = "waffle",
            description = "Combines standard double crochet with Front Post Double Crochet to create a deep grid/waffle texture.",
            category = "Texture Stitches",
            difficulty = "Easy",
            videoUrl = "https://www.youtube.com/watch?v=B9Jk7o6pT4g",
            tips = "Very thick and squishy, ideal for dishcloths, pot holders, and winter afghans."
        )
    )
}

