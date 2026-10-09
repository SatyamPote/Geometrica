# Parse all 100 aliens from user input into clean Kotlin and Python structures
raw_data = """
1. AI_DRIFT_01   EASY     drifts horizontally across screen; skill: "sway-shot" - fires while drifting left-right; ID: "drift-01"
2. AI_DRIFT_02   EASY     faster drift speed; skill: "zigzag" - drifts in sine wave pattern; ID: "drift-02"
3. AI_DRIFT_03   MEDIUM   drift with rotating shield; skill: "reflector" - bounces player shots; ID: "drift-03"
4. AI_TRACK_01   MEDIUM   tracks player position; skill: "lead-shot" - anticipates player movement; ID: "track-01"
5. AI_TRACK_02   HARD     faster tracking; skill: "homing" - splits into 3 homing projectiles; ID: "track-02"
6. AI_TRACK_03   VERY_HARD accelerates when player fires; skill: "predict" - predicts player path 2s ahead; ID: "track-03"
7. AI_DIVE_01    EASY     dives from top of screen; skill: "drop-shot" fires downward when near player; ID: "dive-01"
8. AI_DIVE_02    MEDIUM   dives at angle; skill: "dive-bomb" accelerates toward player; ID: "dive-02"
9. AI_DIVE_03    HARD     multiple dive patterns; skill: "dive-loop" loops around player; ID: "dive-03"
10. AI_WEAVE_01  MEDIUM   weaves left-right in horizontal rows; skill: "pattern" follows preset weaving; ID: "weave-01"
11. AI_WEAVE_02  HARD     weaves with changing pattern; skill: "adaptive-weave" changes pattern when shot; ID: "weave-02"
12. AI_WEAVE_03  VERY_HARD erratic weaving; skill: "random-weave" unpredictable movement; ID: "weave-03"
13. AI_GUN_01    MEDIUM   fires straight projectiles; skill: "burst" - 3 shots rapid-fire; ID: "gun-01"
14. AI_GUN_02    HARD     faster fire rate; skill: "spread" - fires 5-way spread shot; ID: "gun-02"
15. AI_GUN_03    FINAL    continuous fire mode; skill: "laser" - continuous beam for 5s; ID: "gun-03"
16. AI_BURST_01  EASY     explodes on death; skill: "cluster" - bursts into 3 smaller enemies; ID: "burst-01"
17. AI_BURST_02  MEDIUM   explodes after 2s delay; skill: "radius" - radial shots in all directions; ID: "burst-02"
18. AI_BURST_03  HARD     homing burst; skill: "homing-burst" tracks player before exploding; ID: "burst-03"
19. AI_SPIRAL_01 MEDIUM   spirals outward from center; skill: "expand" - increases radius over time; ID: "spiral-01"
20. AI_SPIRAL_02 HARD     spiral that tightens; skill: "contract" - decreases radius, speeds up; ID: "spiral-02"
21. AI_SPIRAL_03 VERY_HARD dual spiral opposite directions; skill: "interlock" interweaving patterns; ID: "spiral-03"
22. AI_SHIELD_01 MEDIUM   has shield that blocks shots; skill: "regen" - shield regenerates if not hit; ID: "shield-01"
23. AI_SHIELD_02 HARD     rotating shield; skill: "drain" - drains player energy when blocking; ID: "shield-02"
24. AI_SHIELD_03 VERY_HARD phased shield; skill: "invuln" - 3s invulnerability after shield break; ID: "shield-03"
25. AI_KAMI_01   EASY     kamikaze dives at player; skill: "crash" - explodes on contact; ID: "kami-01"
26. AI_KAMI_02   MEDIUM   delayed kamikaze; skill: "suicide-run" waits then dives; ID: "kami-02"
27. AI_KAMI_03   HARD     multiple kamikaze; skill: "swarm" - 3 consecutive kamikaze; ID: "kami-03"
28. AI_SNIPER_01 MEDIUM   fires slow projectile; skill: "weak-point" targets player hull; ID: "sniper-01"
29. AI_SNIPER_02 HARD     faster projectile; skill: "predict-lead" leads moving target; ID: "sniper-02"
30. AI_SNIPER_03 FINAL    instant-hit sniper; skill: "one-shot" deals 2 hits; ID: "sniper-03"
31. AI_SPLIT_01  MEDIUM   splits into 2 when damaged; skill: "duplicate" creates copy; ID: "split-01"
32. AI_SPLIT_02  HARD     splits into 3 at 50% HP; skill: "tri-clone" 3 separate entities; ID: "split-02"
33. AI_SPLIT_03  FINAL    splits into 4; skill: "quad-clone" 4 entities with shared HP; ID: "split-03"
34. AI_STRAFE_01 MEDIUM   moves horizontally back-and-forth; skill: "sweep" sweeps across screen; ID: "strafe-01"
35. AI_STRAFE_02 HARD     faster strafe; skill: "dual-sweep" two patterns alternating; ID: "strafe-02"
36. AI_STRAFE_03 VERY_HARD erratic strafe; skill: "random-sweep" unpredictable patterns; ID: "strafe-03"
37. AI_DRIFT_04  MEDIUM   drifts with diagonal pattern; skill: "diagonal-sway" 45° angle shifts; ID: "drift-04"
38. AI_DRIFT_05  HARD     faster diagonal drift; skill: "rapid-diagonal" quick direction changes; ID: "drift-05"
39. AI_TRACK_04  MEDIUM   tracks but slower; skill: "slow-learn" adapts after 5 shots; ID: "track-04"
40. AI_DIVE_04   MEDIUM   dive from sides; skill: "side-dive" enters from screen edges; ID: "dive-04"
41. AI_WEAVE_04  MEDIUM   weaves in vertical columns; skill: "column-weave" vertical weaving; ID: "weave-04"
42. AI_GUN_04    MEDIUM   fires bouncing shots; skill: "bank-shot" bullets bounce off walls; ID: "gun-04"
43. AI_BURST_04  MEDIUM   burst that splits into 2; skill: "split-burst" 2 projectiles from 1; ID: "burst-04"
44. AI_SPIRAL_04 MEDIUM   spiral with gap; skill: "gap-spiral" has opening for player; ID: "spiral-04"
45. AI_SHIELD_04 MEDIUM   shield that moves; skill: "moving-shield" follows player horizontally; ID: "shield-04"
46. AI_KAMI_04   MEDIUM   fake kamikaze; skill: "decoy" pretends to dive then stops; ID: "kami-04"
47. AI_SNIPER_04 MEDIUM   sniper with lead indicator; skill: "clear-indicator" shows aim line; ID: "sniper-04"
48. AI_SPLIT_04  MEDIUM   splits into 2 at distance; skill: "long-split" splits after traveling; ID: "split-04"
49. AI_STRAFE_04 MEDIUM   strafe with gaps; skill: "gap-strafe" has openings during movement; ID: "strafe-04"
50. AI_DRIFT_06  HARD     very fast drift; skill: "speed-drift" max speed constant; ID: "drift-06"
51. AI_TRACK_05  EASY     easy tracking; skill: "-basic-track" simple pursuit; ID: "track-05"
52. AI_DIVE_05   EASY     slow dive; skill: "slow-dive" easy to avoid; ID: "dive-05"
53. AI_WEAVE_05  EASY     slow weave; skill: "slow-weave" easy pattern; ID: "weave-05"
54. AI_GUN_05    EASY     slow fire; skill: "slow-gun" easy to dodge; ID: "gun-05"
55. AI_BURST_05  EASY     slow burst; skill: "slow-burst" easy timing; ID: "burst-05"
56. AI_SPIRAL_05 EASY     slow spiral; skill: "slow-spiral" easy to weave through; ID: "spiral-05"
57. AI_SHIELD_05 EASY     weak shield; skill: "easy-shield" breaks quickly; ID: "shield-05"
58. AI_KAMI_05   EASY     slow kamikaze; skill: "slow-dive" easy to shoot; ID: "kami-05"
59. AI_SNIPER_05 EASY     slow projectile; skill: "easy-sniper" slow speed; ID: "sniper-05"
60. AI_SPLIT_05  EASY     slow split; skill: "easy-split" splits late; ID: "split-05"
61. AI_STRAFE_05 EASY     slow strafe; skill: "slow-sweep" easy to pass; ID: "strafe-05"
62. AI_DRIFT_07  HARD     erratic drift; skill: "random-drift" unpredictable angles; ID: "drift-07"
63. AI_TRACK_06  HARD     very fast tracking; skill: "instant-track" tracks instantly; ID: "track-06"
64. AI_DIVE_06   HARD     rapid dive; skill: "fast-dive" quick descent; ID: "dive-06"
65. AI_WEAVE_06  HARD     very fast weave; skill: "rapid-weave" quick direction changes; ID: "weave-06"
66. AI_GUN_06    HARD     rapid fire; skill: "rapid-gun" high fire rate; ID: "gun-06"
67. AI_BURST_06  HARD     fast burst; skill: "fast-burst" quick explosions; ID: "burst-06"
68. AI_SPIRAL_06 HARD     fast spiral; skill: "fast-spiral" quick rotation; ID: "spiral-06"
69. AI_SHIELD_06 HARD     strong shield; skill: "strong-shield" high HP; ID: "shield-06"
70. AI_KAMI_06   HARD     fast kamikaze; skill: "kamikaze-speed" quick dive; ID: "kami-06"
71. AI_SNIPER_06 HARD     fast projectile; skill: "fast-sniper" high speed; ID: "sniper-06"
72. AI_SPLIT_06  HARD     fast split; skill: "fast-split" splits quickly; ID: "split-06"
73. AI_STRAFE_06 HARD     fast strafe; skill: "fast-sweep" quick movement; ID: "strafe-06"
74. AI_DRIFT_08  VERY_HARD unpredictable drift; skill: "chaos-drift" random angles; ID: "drift-08"
75. AI_TRACK_07  VERY_HARD tracking + shooting; skill: "track-shoot" tracks while firing; ID: "track-07"
76. AI_DIVE_07   VERY_HARD multiple dives; skill: "multidive" 3 simultaneous dives; ID: "dive-07"
77. AI_WEAVE_07  VERY_HARD complex weave; skill: "complex-weave" intricate patterns; ID: "weave-07"
78. AI_GUN_07    VERY_HARD spread + rapid; skill: "spread-rapid" spread + high rate; ID: "gun-07"
79. AI_BURST_07  VERY_HARD homing burst; skill: "homing-burst-track" tracks then bursts; ID: "burst-07"
80. AI_SPIRAL_07 VERY_HARD dual spiral; skill: "dual-spiral" two opposite spirals; ID: "spiral-07"
81. AI_SHIELD_07 VERY_HARD triple shield; skill: "triple-shield" 3 layered shields; ID: "shield-07"
82. AI_KAMI_07   VERY_HARD kamikaze swarm; skill: "kamikaze-swarm" 5 consecutive; ID: "kami-07"
83. AI_SNIPER_07 VERY_HARD piercing shot; skill: "piercing-sniper" goes through walls; ID: "sniper-07"
84. AI_SPLIT_07  VERY_HARD quad split; skill: "quad-split" 4 way split; ID: "split-07"
85. AI_STRAFE_07 VERY_HARD random strafe; skill: "random-strafe" unpredictable; ID: "strafe-07"
86. AI_DRIFT_09  FINAL    boss-phase drift; skill: "boss-drift" drift during boss phases; ID: "drift-09"
87. AI_TRACK_08  FINAL    boss-phase track; skill: "boss-track" track during boss fights; ID: "track-08"
88. AI_DIVE_08   FINAL    boss-phase dive; skill: "boss-dive" dive during boss encounters; ID: "dive-08"
89. AI_WEAVE_08  FINAL    boss-phase weave; skill: "boss-weave" weave during boss battles; ID: "weave-08"
90. AI_GUN_08    FINAL    boss-phase gun; skill: "boss-gun" gun during boss fights; ID: "gun-08"
91. AI_BURST_08  FINAL    boss-phase burst; skill: "boss-burst" burst during boss phases; ID: "burst-08"
92. AI_SPIRAL_08 FINAL    boss-phase spiral; skill: "boss-spiral" spiral during boss fights; ID: "spiral-08"
93. AI_SHIELD_08 FINAL    boss-phase shield; skill: "boss-shield" shield during boss encounters; ID: "shield-08"
94. AI_KAMI_08   FINAL    boss-phase kamikaze; skill: "boss-kami" kamikaze during boss phases; ID: "kami-08"
95. AI_SNIPER_08 FINAL    boss-phase sniper; skill: "boss-sniper" sniper during boss fights; ID: "sniper-08"
96. AI_SPLIT_08  FINAL    boss-phase split; skill: "boss-split" split during boss encounters; ID: "split-08"
97. AI_STRAFE_08 FINAL    boss-phase strafe; skill: "boss-strafe" strafe during boss battles; ID: "strafe-08"
98. AI_DRIFT_10  CUSTOM   custom drift behavior; skill: "custom-drift" player-defined; ID: "drift-10"
99. AI_TRACK_09  CUSTOM   custom tracking; skill: "custom-track" player-defined; ID: "track-09"
100. AI_DIVE_09   CUSTOM   custom dive; skill: "custom-dive" player-defined; ID: "dive-10"
"""

import re

items = []
for line in raw_data.strip().split('\n'):
    line = line.strip()
    if not line:
        continue
    # Format: N. CODE DIFF ... ID: "..."
    # Examples:
    # 33. AI_SPLIT_03  FINAL    splits into 4; skill: "quad-clone" 4 entities with shared HP; ID: "split-03"
    # 51. AI_TRACK_05  EASY     easy tracking; skill: "-basic-track" simple pursuit; ID: "track-05"
    m_num = re.match(r'^(\d+)\.\s+([A-Z0-9_]+)\s+([A-Z_]+)\s+(.+)$', line)
    if m_num:
        num, code, diff, rest = m_num.groups()
        # extract ID
        m_id = re.search(r'ID:\s*"([^"]+)"', rest)
        alien_id = m_id.group(1) if m_id else f"alien-{num}"
        # extract skill
        m_skill = re.search(r'skill:\s*"([^"]+)"(?:\s*-\s*([^;]+))?', rest)
        skill = m_skill.group(1) if m_skill else "normal"
        skill_detail = m_skill.group(2) if m_skill and m_skill.group(2) else ""
        desc = rest.split(';')[0].strip()
        items.append({
            "idx": int(num) - 1,
            "code": code,
            "diff": diff,
            "desc": desc,
            "skill": skill,
            "skill_detail": skill_detail.strip(),
            "id": alien_id
        })
    else:
        print("FAILED TO MATCH:", line)

print(f"Successfully matched {len(items)} aliens!")

# Write Kotlin AlienCatalog.kt
kt_code = [
    "package com.nebulastrike.game\n",
    "/**",
    " * Complete catalog of all 100 unique small alien enemies & space ships,",
    " * each with their dedicated skill, AI archetype, ID, and combat metrics.",
    " */",
    "data class AlienSpecInfo(",
    "    val code: String,",
    "    val id: String,",
    "    val diff: String,",
    "    val skill: String,",
    "    val aiType: Int,",
    "    val hp: Float,",
    "    val spd: Float,",
    "    val r: Float,",
    "    val score: Int,",
    "    val fireRate: Float,",
    "    val p1: Float = 0f,",
    "    val p2: Float = 0f,",
    "    val splitSpec: Int = -1",
    ")\n",
    "val ALL_100_ALIEN_SPECS = listOf("
]

ai_map = {
    "DRIFT": "AI_DRIFT",
    "TRACK": "AI_TRACK",
    "DIVE": "AI_DIVE",
    "WEAVE": "AI_WEAVE",
    "GUN": "AI_GUN",
    "BURST": "AI_BURST",
    "SPIRAL": "AI_SPIRAL",
    "SHIELD": "AI_SHIELD",
    "KAMI": "AI_KAMI",
    "SNIPER": "AI_SNIPER",
    "SPLIT": "AI_SPLIT",
    "STRAFE": "AI_STRAFE"
}

diff_multipliers = {
    "EASY": (1.0, 1.0, 100),
    "MEDIUM": (1.8, 1.15, 200),
    "HARD": (2.8, 1.3, 350),
    "VERY_HARD": (4.0, 1.45, 550),
    "FINAL": (6.5, 1.6, 900),
    "CUSTOM": (3.5, 1.35, 450)
}

for it in items:
    # Determine AI family
    ai_key = "DRIFT"
    for k in ai_map.keys():
        if f"_{k}_" in it["code"]:
            ai_key = k
            break
    ai_enum = ai_map[ai_key]
    
    hp_mult, spd_mult, base_score = diff_multipliers.get(it["diff"], (1.5, 1.1, 150))
    hp = round(18.0 * hp_mult, 1)
    spd = round(1.0 * spd_mult, 2)
    radius = 16.0 + (it["idx"] % 6) * 1.5
    fire_rate = round(max(0.8, 2.5 / spd_mult), 2)
    p1 = 0.0
    p2 = 0.0
    split = -1
    
    if ai_key == "GUN":
        p1 = 3.0 if it["diff"] in ["HARD", "VERY_HARD", "FINAL"] else 2.0
    elif ai_key == "BURST":
        p1 = 4.0
    elif ai_key == "SPIRAL":
        p1 = 8.0
    elif ai_key == "SHIELD":
        p1 = 2.0
        p2 = 1.0
    elif ai_key == "SPLIT":
        p1 = 2.0
        split = (it["idx"] + 1) % 100
        
    kt_code.append(
        f'    AlienSpecInfo("{it["code"]}", "{it["id"]}", "{it["diff"]}", "{it["skill"]}", {ai_enum}, {hp}f, {spd}f, {radius}f, {int(base_score + it["idx"] * 5)}, {fire_rate}f, {p1}f, {p2}f, {split}),'
    )

kt_code.append(")\n")

with open(r"c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\AlienCatalog.kt", "w") as f:
    f.write("\n".join(kt_code))

print("Wrote AlienCatalog.kt successfully!")
