with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\all_100_roster.kt', 'r') as f:
    roster_code = f.read().strip()

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\World.kt', 'r') as f:
    world_code = f.read()

start_marker = "/** Exactly 50 distinct enemy planes, ordered easy -> brutal. */\nval ENEMY_ROSTER = listOf("
end_marker = "EnemySpec(\"OMEGA\", AI_GUN, 1400f, 0.7f, 70f, 1800, 1.7f, p1 = 6f)\n)"

# also update EnemySpec data class to include skill
old_spec = """data class EnemySpec(
    val name: String,
    val ai: Int,
    val hp: Float,
    val spd: Float,
    val r: Float,
    val score: Int,
    val fire: Float,
    val p1: Float = 0f,
    val p2: Float = 0f,
    val split: Int = -1
)"""

new_spec = """data class EnemySpec(
    val name: String,
    val ai: Int,
    val hp: Float,
    val spd: Float,
    val r: Float,
    val score: Int,
    val fire: Float,
    val p1: Float = 0f,
    val p2: Float = 0f,
    val split: Int = -1,
    val skill: String = ""
)"""

if old_spec in world_code:
    world_code = world_code.replace(old_spec, new_spec)
    print("Replaced EnemySpec signature!")
else:
    print("Could not find old_spec!")

new_roster_header = "/** Exactly 100 distinct small alien enemies & spaceships with individual skills. */\n" + roster_code

idx1 = world_code.find("val ENEMY_ROSTER = listOf(")
idx2 = world_code.find("EnemySpec(\"OMEGA\", AI_GUN, 1400f, 0.7f, 70f, 1800, 1.7f, p1 = 6f)\n)") + len("EnemySpec(\"OMEGA\", AI_GUN, 1400f, 0.7f, 70f, 1800, 1.7f, p1 = 6f)\n)")

if idx1 != -1 and idx2 != -1:
    world_code = world_code[:idx1] + roster_code + world_code[idx2:]
    print("Replaced ENEMY_ROSTER successfully!")
    with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\app\src\main\java\com\nebulastrike\game\World.kt', 'w') as f:
        f.write(world_code)
    print("Updated World.kt!")
else:
    print(f"Indices not found: {idx1}, {idx2}")
