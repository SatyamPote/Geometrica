import re

raw_ships = """
1. interceptor      EASY     small fast ship; power: 2, energy: 3, speed: 5, durability: 1, health: 30; cost: 500 coins; skill: "rapid-fire" - 20% faster shot speed; ID: "ship-01"
2. striker          EASY     balanced ship; power: 3, energy: 3, speed: 3, durability: 2, health: 50; cost: 800 coins; skill: "spread-shot" - 3 projectiles; ID: "ship-02"
3. defender         EASY     tough ship; power: 2, energy: 2, speed: 2, durability: 4, health: 80; cost: 1000 coins; skill: "shield-bubble" - auto shield; ID: "ship-03"
4. raider           MEDIUM   agile ship; power: 3, energy: 4, speed: 4, durability: 2, health: 60; cost: 1500 coins; skill: "dash" - brief invulnerability; ID: "ship-04"
5. bomber           MEDIUM   heavy shot; power: 4, energy: 2, speed: 2, durability: 3, health: 70; cost: 1500 coins; skill: "spread-bomb" - wide shot pattern; ID: "ship-05"
6. scout            MEDIUM   fast mobility; power: 2, energy: 4, speed: 5, durability: 1, health: 40; cost: 1200 coins; skill: "tracking" - shots lead target; ID: "ship-06"
7. vanguard         MEDIUM   all-rounder; power: 3, energy: 3, speed: 3, durability: 3, health: 65; cost: 1800 coins; skill: "pierce" - shots pass through enemies; ID: "ship-07"
8. extinguisher     MEDIUM   support ship; power: 2, energy: 5, speed: 3, durability: 2, health: 55; cost: 1600 coins; skill: "heal-orbs" - occasional heart drops; ID: "ship-08"
9. pursuer          HARD     fast aggressive; power: 4, energy: 3, speed: 4, durability: 2, health: 65; cost: 2500 coins; skill: "focus-fire" - 25% faster fire rate; ID: "ship-09"
10. annihilator     HARD     high damage; power: 5, energy: 2, speed: 2, durability: 3, health: 80; cost: 3000 coins; skill: "charge-shot" - charge for bigger shot; ID: "ship-10"
11. guardian        HARD     tanky; power: 3, energy: 3, speed: 2, durability: 5, health: 120; cost: 3500 coins; skill: "damage-reflect" - reflects 20% damage; ID: "ship-11"
12. interceptor-elite HARD    upgraded interceptor; power: 3, energy: 4, speed: 6, durability: 2, health: 70; cost: 4000 coins; skill: "afterburner" - short speed burst; ID: "ship-12"
13. striker-elite   HARD     upgraded striker; power: 4, energy: 4, speed: 4, durability: 3, health: 85; cost: 4500 coins; skill: "quad-shot" - 4 projectiles; ID: "ship-13"
14. defender-elite  HARD    upgraded defender; power: 3, energy: 3, speed: 2, durability: 6, health: 150; cost: 5000 coins; skill: "dual-shield" - two auto shields; ID: "ship-14"
15. raider-elite    VERY_HARD upgraded raider; power: 4, energy: 5, speed: 5, durability: 3, health: 90; cost: 6000 coins; skill: "velocity-dash" - longer dash; ID: "ship-15"
16. bomber-elite    VERY_HARD upgraded bomber; power: 5, energy: 3, speed: 3, durability: 4, health: 100; cost: 6500 coins; skill: "mega-bomb" - screen-clearing blast; ID: "ship-16"
17. scout-elite     VERY_HARD upgraded scout; power: 3, energy: 5, speed: 6, durability: 2, health: 50; cost: 5500 coins; skill: "homing-all" - all shots homing; ID: "ship-17"
18. vanguard-elite  VERY_HARD upgraded vanguard; power: 4, energy: 4, speed: 4, durability: 4, health: 100; cost: 7000 coins; skill: "spread-pierce" - spread that passes through; ID: "ship-18"
19. extinguisher-elite VERY_HARD upgraded extinguisher; power: 3, energy: 6, speed: 3, durability: 3, health: 80; cost: 7500 coins; skill: "mass-heal" - frequent heart drops; ID: "ship-19"
20. pursuer-elite   FINAL     upgraded pursuer; power: 5, energy: 4, speed: 5, durability: 3, health: 110; cost: 8000 coins; skill: "berserk" - 30% faster but takes more damage; ID: "ship-20"
21. phoenix         EASY     bird-shaped ship; power: 2, energy: 3, speed: 3, durability: 2, health: 40; cost: 600 coins; skill: "fire-trail" - damages touchers; ID: "ship-21"
22. arrowhead       EASY     pointed ship; power: 3, energy: 2, speed: 4, durability: 1, health: 35; cost: 700 coins; skill: "penetrating" - shots go through obstacles; ID: "ship-22"
23. luna            EASY     crescent moon shape; power: 2, energy: 4, speed: 3, durability: 2, health: 45; cost: 800 coins; skill: "phase-shift" - brief intangibility; ID: "ship-23"
24. comet           EASY     trailing tail ship; power: 2, energy: 3, speed: 4, durability: 1, health: 38; cost: 650 coins; skill: "tail-attack" - trailing shot projectiles; ID: "ship-24"
25. mercury         MEDIUM   circular ship; power: 3, energy: 3, speed: 3, durability: 2, health: 55; cost: 1400 coins; skill: "orbit" - small orb circles shooting; ID: "ship-25"
26. venus           MEDIUM   twin-ball ship; power: 3, energy: 3, speed: 3, durability: 2, health: 58; cost: 1450 coins; skill: "dual-orbit" - two orbiting shots; ID: "ship-26"
27. mars            MEDIUM   red-dot ship; power: 4, energy: 2, speed: 4, durability: 2, health: 60; cost: 1600 coins; skill: " rapid-burst" - quick successive shots; ID: "ship-27"
28. jupiter         MEDIUM   large ringed ship; power: 3, energy: 4, speed: 2, durability: 3, health: 70; cost: 1500 coins; skill: "ring-damage" - ring around ship damages touchers; ID: "ship-28"
29. saturn          MEDIUM   hexagonal ship; power: 3, energy: 3, speed: 3, durability: 3, health: 62; cost: 1550 coins; skill: "hex-shot" - 6-way shot; ID: "ship-29"
30. uranus          MEDIUM   tilted ship; power: 2, energy: 4, speed: 4, durability: 2, health: 52; cost: 1300 coins; skill: "side-thrust" - lateral movement shot; ID: "ship-30"
31. neptune         MEDIUM   elongated ship; power: 3, energy: 3, speed: 3, durability: 2, health: 56; cost: 1350 coins; skill: "length-shot" - long ranged shot; ID: "ship-31"
32. pluto           MEDIUM   small icy ship; power: 2, energy: 3, speed: 4, durability: 1, health: 42; cost: 900 coins; skill: "ice-shots" - shots slow enemies; ID: "ship-32"
33. asteroid        HARD     rocky ship; power: 4, energy: 3, speed: 3, durability: 4, health: 90; cost: 3200 coins; skill: "rock-throw" - launches debris projectiles; ID: "ship-33"
34. comet-hail      HARD     many tail ships; power: 3, energy: 4, speed: 4, durability: 2, health: 75; cost: 3000 coins; skill: "hail-mary" - many quick shots; ID: "ship-34"
35. meteor          HARD     fiery ship; power: 5, energy: 2, speed: 4, durability: 2, health: 85; cost: 3500 coins; skill: "fire-ring" - ring of fire around; ID: "ship-35"
36. nova            VERY_HARD supernova ship; power: 6, energy: 3, speed: 3, durability: 3, health: 150; cost: 5000 coins; skill: "super-nova" - massive explosion on death; ID: "ship-36"
37. quasar          VERY_HARD quasar ship; power: 5, energy: 5, speed: 2, durability: 4, health: 130; cost: 5500 coins; skill: "gravity-well" - pulls nearby coins; ID: "ship-37"
38. black-hole      VERY_HARD dense ship; power: 4, energy: 4, speed: 2, durability: 5, health: 140; cost: 6000 coins; skill: "event-horizon" - slows nearby enemies; ID: "ship-38"
39. singularity     FINAL     black hole ship; power: 5, energy: 6, speed: 2, durability: 6, health: 200; cost: 8000 coins; skill: "spaghettification" - stretches enemies; ID: "ship-39"
40. big-bang        FINAL     explosion ship; power: 7, energy: 4, speed: 3, durability: 3, health: 180; cost: 9000 coins; skill: "big-bang" - screen-wide burst on spawn; ID: "ship-40"
41. vector          EASY     vector-shaped ship; power: 2, energy: 3, speed: 3, durability: 2, health: 40; cost: 550 coins; skill: "directional" - shoots where moving; ID: "ship-41"
42. helix           EASY     spiral ship; power: 2, energy: 3, speed: 3, durability: 1, health: 38; cost: 500 coins; skill: "spin-shot" - shots spiral out; ID: "ship-42"
43. vortex          MEDIUM   vortex ship; power: 3, energy: 4, speed: 3, durability: 2, health: 55; cost: 1300 coins; skill: "swirl" - shots in swirl pattern; ID: "ship-43"
44. spiral          MEDIUM   corkscrew ship; power: 3, energy: 3, speed: 4, durability: 2, health: 58; cost: 1350 coins; skill: "coil-shot" - charged spiral shot; ID: "ship-44"
45. coil            MEDIUM   spring ship; power: 3, energy: 3, speed: 3, durability: 2, health: 56; cost: 1200 coins; skill: "compress" - focused charged shot; ID: "ship-45"
46. spring          MEDIUM   springy ship; power: 2, energy: 4, speed: 4, durability: 2, health: 50; cost: 1100 coins; skill: "bounce" - shots bounce off walls; ID: "ship-46"
47. bounce          MEDIUM   bouncy ship; power: 3, energy: 3, speed: 3, durability: 2, health: 54; cost: 1150 coins; skill: "ricochet" - shots ricochet; ID: "ship-47"
48. ripple          MEDIUM   ripple ship; power: 3, energy: 3, speed: 3, durability: 2, health: 52; cost: 1200 coins; skill: "wave" - spreads shot in wave; ID: "ship-48"
49. wave            MEDIUM   wave ship; power: 2, energy: 4, speed: 3, durability: 2, health: 50; cost: 1150 coins; skill: "frequency" - shot frequency increases; ID: "ship-49"
50. resonance       MEDIUM   resonance ship; power: 4, energy: 3, speed: 3, durability: 2, health: 60; cost: 1400 coins; skill: "harmonic" - shots chain between enemies; ID: "ship-50"
51. echo            MEDIUM   echo ship; power: 3, energy: 3, speed: 4, durability: 2, health: 55; cost: 1300 coins; skill: "repeater" - shots repeat after delay; ID: "ship-51"
52. repeat          MEDIUM   repeater ship; power: 3, energy: 4, speed: 3, durability: 2, health: 58; cost: 1350 coins; skill: "loop" - shots loop around screen; ID: "ship-52"
53. loop            MEDIUM   loop ship; power: 3, energy: 3, speed: 4, durability: 2, health: 56; cost: 1300 coins; skill: "orbit-loop" - shots orbit then fire; ID: "ship-53"
54. orbit           MEDIUM   orbit ship; power: 3, energy: 4, speed: 3, durability: 2, health: 58; cost: 1350 coins; skill: "dual-orbit" - two rotating shots; ID: "ship-54"
55. rotate          MEDIUM   rotating ship; power: 3, energy: 3, speed: 3, durability: 2, health: 54; cost: 1250 coins; skill: "spin-up" - shot speed increases over time; ID: "ship-55"
56. spin            MEDIUM   spin ship; power: 2, energy: 4, speed: 4, durability: 2, health: 52; cost: 1200 coins; skill: "rapid-spin" - fast rotation shot pattern; ID: "ship-56"
57. turn            MEDIUM   turning ship; power: 3, energy: 3, speed: 3, durability: 2, health: 55; cost: 1250 coins; skill: "quick-turn" - fast rotation shot; ID: "ship-57"
58. pivot           MEDIUM   pivot ship; power: 3, energy: 3, speed: 4, durability: 2, health: 57; cost: 1300 coins; skill: "pivot-shot" - shoots while pivoting; ID: "ship-58"
59. swivel          MEDIUM   swivel ship; power: 3, energy: 3, speed: 3, durability: 2, health: 53; cost: 1200 coins; skill: "swivel-shot" - gun swivels 360°; ID: "ship-59"
60. pivot-elite     HARD    upgraded pivot; power: 4, energy: 4, speed: 4, durability: 3, health: 75; cost: 3500 coins; skill: "double-pivot" - two pivoting guns; ID: "ship-60"
61. swivel-elite    HARD    upgraded swivel; power: 4, energy: 3, speed: 3, durability: 3, health: 78; cost: 3600 coins; skill: "360-chain" - chainshots 360°; ID: "ship-61"
62. spin-elite      HARD    upgraded spin; power: 5, energy: 4, speed: 5, durability: 3, health: 85; cost: 4000 coins; skill: "hyper-spin" - extreme speed spin; ID: "ship-62"
63. orbit-elite     HARD    upgraded orbit; power: 4, energy: 5, speed: 4, durability: 3, health: 80; cost: 4200 coins; skill: "triple-orbit" - three rotating shots; ID: "ship-63"
64. rotate-elite    HARD    upgraded rotate; power: 4, energy: 4, speed: 4, durability: 3, health: 76; cost: 3800 coins; skill: "accelerated-spin" - faster spin acceleration; ID: "ship-64"
65. vector-elite    VERY_HARD upgraded vector; power: 3, energy: 5, speed: 5, durability: 3, health: 82; cost: 4500 coins; skill: "auto-aim" - slight auto-aim assistance; ID: "ship-65"
66. helix-elite     VERY_HARD upgraded helix; power: 4, energy: 5, speed: 5, durability: 3, health: 88; cost: 4800 coins; skill: "tight-coil" - tighter spiral shot; ID: "ship-66"
67. vortex-elite    VERY_HARD upgraded vortex; power: 4, energy: 5, speed: 4, durability: 3, health: 84; cost: 4600 coins; skill: "vortex-ring" - ring of shots; ID: "ship-67"
68. spiral-elite    VERY_HARD upgraded spiral; power: 5, energy: 4, speed: 5, durability: 3, health: 90; cost: 5000 coins; skill: "rapid-spiral" - fast spiral shots; ID: "ship-68"
69. coil-elite      VERY_HARD upgraded coil; power: 4, energy: 5, speed: 4, durability: 3, health: 86; cost: 4700 coins; skill: "super-compress" - max charged shot; ID: "ship-69"
70. spring-elite    VERY_HARD upgraded spring; power: 3, energy: 5, speed: 5, durability: 3, health: 82; cost: 4650 coins; skill: "super-bounce" - extreme bounce height; ID: "ship-70"
71. ripple-elite    VERY_HARD upgraded ripple; power: 4, energy: 4, speed: 4, durability: 3, health: 80; cost: 4500 coins; skill: "amplitude" - shot amplitude increases; ID: "ship-71"
72. wave-elite      VERY_HARD upgraded wave; power: 3, energy: 5, speed: 4, durability: 3, health: 85; cost: 4800 coins; skill: "frequency-mod" - frequency modulation; ID: "ship-72"
73. resonance-elite VERY_HARD upgraded resonance; power: 5, energy: 4, speed: 4, durability: 3, health: 88; cost: 5000 coins; skill: "harmonic-chain" - longer chain; ID: "ship-73"
74. echo-elite      VERY_HARD upgraded echo; power: 4, energy: 4, speed: 5, durability: 3, health: 82; cost: 4700 coins; skill: "repeater-long" - longer repeat delay; ID: "ship-74"
75. repeat-elite    VERY_HARD upgraded repeat; power: 4, energy: 4, speed: 5, durability: 3, health: 84; cost: 4850 coins; skill: "loop-extended" - longer loop; ID: "ship-75"
76. loop-elite      VERY_HARD upgraded loop; power: 3, energy: 5, speed: 5, durability: 3, health: 80; cost: 4600 coins; skill: "orbit-extended" - larger orbit; ID: "ship-76"
77. orbit-elite2    VERY_HARD second orbit upgrade; power: 4, energy: 5, speed: 4, durability: 3, health: 81; cost: 4900 coins; skill: "quad-orbit" - four rotating; ID: "ship-77"
78. rotate-elite2   VERY_HARD second rotate upgrade; power: 5, energy: 4, speed: 5, durability: 3, health: 83; cost: 5100 coins; skill: "dual-spin" - two spinning guns; ID: "ship-78"
79. vector-final    FINAL     final vector; power: 4, energy: 6, speed: 6, durability: 4, health: 120; cost: 7000 coins; skill: "perfect-vector" - perfect direction shot; ID: "ship-79"
80. helix-final     FINAL     final helix; power: 5, energy: 6, speed: 5, durability: 4, health: 130; cost: 7500 coins; skill: "final-coil" - ultimate spiral; ID: "ship-80"
81. vortex-final    FINAL     final vortex; power: 5, energy: 6, speed: 4, durability: 5, health: 140; cost: 8000 coins; skill: " ultimate-vortex" - massive vortex; ID: "ship-81"
82. spiral-final    FINAL     final spiral; power: 6, energy: 5, speed: 5, durability: 4, health: 150; cost: 8500 coins; skill: "ultimate-spiral" - galaxy-spanning spiral; ID: "ship-82"
83. coil-final      FINAL     final coil; power: 5, energy: 6, speed: 4, durability: 5, health: 135; cost: 8200 coins; skill: "super-charged" - max charged shot; ID: "ship-83"
84. spring-final    FINAL     final spring; power: 4, energy: 6, speed: 5, durability: 5, health: 150; cost: 8800 coins; skill: "hyper-spring" - extreme bounce; ID: "ship-84"
85. ripple-final    FINAL     final ripple; power: 5, energy: 5, speed: 5, durability: 4, health: 140; cost: 8600 coins; skill: "ultra-amplitude" - max amplitude; ID: "ship-85"
86. wave-final      FINAL     final wave; power: 4, energy: 6, speed: 5, durability: 4, health: 145; cost: 8900 coins; skill: "wave-master" - control waves; ID: "ship-86"
87. resonance-final FINAL     final resonance; power: 6, energy: 5, speed: 5, durability: 4, health: 155; cost: 9200 coins; skill: "cosmic-harmony" - universal chain; ID: "ship-87"
88. echo-final      FINAL     final echo; power: 5, energy: 5, speed: 6, durability: 4, health: 138; cost: 9000 coins; skill: "eternal-repeater" - infinite repeat; ID: "ship-88"
89. repeat-final    FINAL     final repeat; power: 5, energy: 5, speed: 6, durability: 4, health: 142; cost: 9100 coins; skill: "infinite-loop" - never ending; ID: "ship-89"
90. loop-final      FINAL     final loop; power: 4, energy: 6, speed: 5, durability: 4, health: 148; cost: 9050 coins; skill: "permanent-orbit" - permanent orbit; ID: "ship-90"
91. orbit-final     FINAL     final orbit; power: 5, energy: 6, speed: 5, durability: 4, health: 150; cost: 9200 coins; skill: "master-orbit" - perfect orbit; ID: "ship-91"
92. rotate-final    FINAL     final rotate; power: 6, energy: 5, speed: 6, durability: 5, health: 160; cost: 9500 coins; skill: "ultra-spin" - maximum spin; ID: "ship-92"
93. vector-legend   LEGEND    legendary vector; power: 5, energy: 7, speed: 7, durability: 5, health: 200; cost: 12000 coins; skill: "legendary-vector" - mythical shot; ID: "ship-93"
94. helix-legend    LEGEND    legendary helix; power: 6, energy: 7, speed: 6, durability: 5, health: 210; cost: 12500 coins; skill: "legendary-helix" - mythical spiral; ID: "ship-94"
95. vortex-legend   LEGEND    legendary vortex; power: 6, energy: 7, speed: 5, durability: 6, health: 220; cost: 13000 coins; skill: "legendary-vortex" - mythical vortex; ID: "ship-95"
96. spiral-legend   LEGEND    legendary spiral; power: 7, energy: 6, speed: 6, durability: 5, health: 230; cost: 13500 coins; skill: "legendary-spiral" - mythical spiral; ID: "ship-96"
97. coil-legend     LEGEND    legendary coil; power: 6, energy: 7, speed: 5, durability: 6, health: 215; cost: 13200 coins; skill: "legendary-coil" - mythical coil; ID: "ship-97"
98. spring-legend   LEGEND    legendary spring; power: 5, energy: 7, speed: 7, durability: 6, health: 225; cost: 12800 coins; skill: "legendary-spring" - mythical spring; ID: "ship-98"
99. ripple-legend   LEGEND    legendary ripple; power: 6, energy: 6, speed: 6, durability: 5, health: 210; cost: 12600 coins; skill: "legendary-ripple" - mythical ripple; ID: "ship-99"
100. wave-legend     LEGEND    legendary wave; power: 6, energy: 7, speed: 7, durability: 5, health: 220; cost: 13400 coins; skill: "legendary-wave" - mythical wave; ID: "ship-100"
"""

items = []
for line in raw_ships.strip().split('\n'):
    line = line.strip()
    if not line:
        continue
    # 1. interceptor EASY small fast ship; power: 2, energy: 3, speed: 5, durability: 1, health: 30; cost: 500 coins; skill: "rapid-fire" - 20% faster shot speed; ID: "ship-01"
    m_num = re.match(r'^(\d+)\.\s+([a-zA-Z0-9\-_]+)\s+([A-Z_]+)\s+(.+)$', line)
    if not m_num:
        print("Failed to match:", line)
        continue
    num, name, diff, rest = m_num.groups()
    
    # parse stats: power: 2, energy: 3, speed: 5, durability: 1, health: 30; cost: 500 coins; skill: "rapid-fire" ...; ID: "ship-01"
    p_power = int(re.search(r'power:\s*(\d+)', rest).group(1)) if re.search(r'power:\s*(\d+)', rest) else 2
    p_energy = int(re.search(r'energy:\s*(\d+)', rest).group(1)) if re.search(r'energy:\s*(\d+)', rest) else 3
    p_speed = int(re.search(r'speed:\s*(\d+)', rest).group(1)) if re.search(r'speed:\s*(\d+)', rest) else 3
    p_durability = int(re.search(r'durability:\s*(\d+)', rest).group(1)) if re.search(r'durability:\s*(\d+)', rest) else 2
    p_health = int(re.search(r'health:\s*(\d+)', rest).group(1)) if re.search(r'health:\s*(\d+)', rest) else 40
    p_cost = int(re.search(r'cost:\s*(\d+)', rest).group(1)) if re.search(r'cost:\s*(\d+)', rest) else 1000
    m_skill = re.search(r'skill:\s*"([^"]+)"', rest)
    skill = m_skill.group(1).strip() if m_skill else "normal"
    m_id = re.search(r'ID:\s*"([^"]+)"', rest)
    ship_id = m_id.group(1).strip() if m_id else f"ship-{num}"
    
    items.append({
        "num": int(num),
        "name": name.strip(),
        "diff": diff.strip(),
        "power": p_power,
        "energy": p_energy,
        "speed": p_speed,
        "durability": p_durability,
        "health": p_health,
        "cost": p_cost,
        "skill": skill,
        "id": ship_id
    })

print(f"Parsed {len(items)} player ships successfully!")

# Write Kotlin PlayerShipCatalog.kt
kt_lines = [
    "package com.nebulastrike.game\n",
    "/**",
    " * Complete catalog of all 100 player spaceships with unique stats, skills, IDs, and costs.",
    " */",
    "data class PlayerShipInfo(",
    "    val num: Int,",
    "    val name: String,",
    "    val id: String,",
    "    val diff: String,",
    "    val power: Int,",
    "    val energy: Int,",
    "    val speed: Int,",
    "    val durability: Int,",
    "    val health: Int,",
    "    val cost: Int,",
    "    val skill: String",
    ")\n",
    "val ALL_100_PLAYER_SHIPS = listOf("
]

for it in items:
    kt_lines.append(
        f'    PlayerShipInfo({it["num"]}, "{it["name"]}", "{it["id"]}", "{it["diff"]}", {it["power"]}, {it["energy"]}, {it["speed"]}, {it["durability"]}, {it["health"]}, {it["cost"]}, "{it["skill"]}"),'
    )
kt_lines.append(")\n")

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\PlayerShipCatalog.kt', 'w') as f:
    f.write("\n".join(kt_lines))

print("Wrote PlayerShipCatalog.kt successfully!")
