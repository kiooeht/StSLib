# StSLib #
StSLib provides a number of keywords and mechanics for other mods to use.

StSLib aims to implement common mod features so each mod doesn't have to reimplement them.

### Requires:
 * [BaseMod](https://github.com/daviscook477/BaseMod/releases)
 * [ModTheSpire](https://github.com/kiooeht/ModTheSpire)
 
# Features

## Keywords
Keyword | Description | Field
--- | --- | ---
Autoplay | This card automatically plays itself when drawn. |  `AutoplayField.autoplay`
Fleeting | This card Purges and is removed from your deck on use. | `FleetingField.fleeting`
Grave | Start each combat with this card in your discard pile. | `GraveField.grave`
Purge | Removed until end of combat. Does NOT go to your exhaust pile. | `AbstractCard.purgeOnUse`
Retain | Not discarded at the end of your turn. | `AlwaysRetainField.alwaysRetain`
Snecko | When drawn, this card randomizes its cost. | `SneckoField.snecko`
Soulbound | Cannot be removed from your deck. | `SoulboundField.soulbound`

## Actions
Class Name | Description
--- | ---
StunMonsterAction | Stuns a monster so it can't act for a turn.
