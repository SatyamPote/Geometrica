import re

with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\parse_100_aliens.py', 'r') as f:
    text = f.read()

raw_data = text.split('raw_data = """')[1].split('"""')[0]

ai_map = {
    'DRIFT': 'AI_DRIFT', 'TRACK': 'AI_TRACK', 'DIVE': 'AI_DIVE',
    'WEAVE': 'AI_WEAVE', 'GUN': 'AI_GUN', 'BURST': 'AI_BURST',
    'SPIRAL': 'AI_SPIRAL', 'SHIELD': 'AI_SHIELD', 'KAMI': 'AI_KAMI',
    'SNIPER': 'AI_SNIPER', 'SPLIT': 'AI_SPLIT', 'STRAFE': 'AI_STRAFE'
}

diff_multipliers = {
    'EASY': (1.0, 1.0, 100),
    'MEDIUM': (1.8, 1.15, 200),
    'HARD': (2.8, 1.3, 350),
    'VERY_HARD': (4.0, 1.45, 550),
    'FINAL': (6.5, 1.6, 900),
    'CUSTOM': (3.5, 1.35, 450)
}

lines = []
for line in raw_data.strip().split('\n'):
    line = line.strip()
    if not line:
        continue
    m_num = re.match(r'^(\d+)\.\s+([A-Z0-9_]+)\s+([A-Z_]+)\s+(.+)$', line)
    if not m_num:
        continue
    num, code, diff, rest = m_num.groups()
    m_id = re.search(r'ID:\s*"([^"]+)"', rest)
    alien_id = m_id.group(1) if m_id else f'alien-{num}'
    m_skill = re.search(r'skill:\s*"([^"]+)"(?:\s*-\s*([^;]+))?', rest)
    skill = m_skill.group(1) if m_skill else 'normal'
    idx = int(num) - 1

    ai_key = 'DRIFT'
    for k in ai_map.keys():
        if f'_{k}_' in code:
            ai_key = k
            break
    ai_enum = ai_map[ai_key]
    hp_mult, spd_mult, base_score = diff_multipliers.get(diff, (1.5, 1.1, 150))
    hp = round(18.0 * hp_mult, 1)
    spd = round(1.0 * spd_mult, 2)
    radius = 16.0 + (idx % 6) * 1.5
    fire_rate = round(max(0.8, 2.5 / spd_mult), 2)
    p1 = 0.0
    p2 = 0.0
    split = -1
    if ai_key == 'GUN':
        p1 = 3.0 if diff in ['HARD', 'VERY_HARD', 'FINAL'] else 2.0
    elif ai_key == 'BURST':
        p1 = 4.0
    elif ai_key == 'SPIRAL':
        p1 = 8.0
    elif ai_key == 'SHIELD':
        p1 = 2.0
        p2 = 1.0
    elif ai_key == 'SPLIT':
        p1 = 2.0
        split = (idx + 1) % 100

    lines.append(f'    EnemySpec("{alien_id}", {ai_enum}, {hp}f, {spd}f, {radius}f, {int(base_score + idx*5)}, {fire_rate}f, {p1}f, {p2}f, {split}, "{skill}"),')

print(f"Generated {len(lines)} items")
with open(r'c:\Users\satya\Documents\BambooKit\Geometrica\SpaceImpactAndroid\all_100_roster.kt', 'w') as f:
    f.write('val ENEMY_ROSTER = listOf(\n' + '\n'.join(lines) + '\n)\n')

print("Wrote all_100_roster.kt successfully!")
