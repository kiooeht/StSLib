## Changelog ##
#### dev ####

#### v1.18.1 ####
* Fix branching upgrade trying to display when transforming a card

#### v1.18.0 ####
* Fix InvisiblePower tooltips appearing on player
* `OnApplyPowerRelic` (erasels)
* `OnAnyPowerAppliedRelic` (erasels)
* Branching upgrade cards (Reina & erasels)

#### v1.17.2 ####
* Updated ZHS translation by Rita-B

#### v1.17.1 ####
* Fix onCardDraw() being called twice on powers
* Deprecate `OnCardDrawPower`

#### v1.17.0 ####
* Make TempHP work on monsters

#### v1.16.0 ####
* Powers
  * `OnPlayerDeathPower`
* Relics
  * `OnPlayerDeathRelic`

#### v1.15.0 ####
* Add color to `TwoAmountPower` (Rin Camelia)

#### v1.14.0 ####
* Powers
  * `BetterOnExhaustPower`
* Allow card selection in `MoveCardsAction` to be sorted
* Allow `OnReceivePower` to change stackAmount
* Allow `BetterOnApplyPowerPower` to change stackAmount
* Use English strings as backup for localization
* Fix `OnLoseBlock` hooks triggering when creature has no block

#### v1.13.2 ####
* Fix fetching from exhaust pile making cards invisible
* Fix `MoveCardsAction` callback not calling if it shortcuts

#### v1.13.1 ####
* Localization
  * Chinese: Fix missing relic and power

#### v1.13.0 ####
* Add callback of cards for `MoveCardsAction`
* Add callback of cards for `FetchAction`

#### v1.12.0 ####
* Localization
  * Russian (Jedi)
  * Simplified Chinese (rainfoxest)
  * Traditional Chinese (rainfoxest)

#### v1.11.0 ####
* Fix SuperRareRelic causing crash if pool is empty
* Fix Stun tooltip not matching icon amount
* Fix Stun not resetting monster's intent correctly if used in an Autoplay card

#### v1.10.0 ####
* Powers
  * `BetterOnApplyPowerPower`
  * `OnMyBlockBrokenPower`
  * `TwoAmountPower`
  * `NonStackablePower`

#### v1.9.0 ####
* Powers
  * `HealthBarRenderPower`
  * `OnLoseTempHpPower`
  * `OnLoseBlockPower`
* Relics
  * `OnLoseTempHpRelic`
  * `OnLoseBlockRelic`

#### v1.8.1 ####
* Fix stun having incorrect intents the next turn

#### v1.8.0 ####
* Relics
  * `BetterOnSmithRelic` (Reina)
  * `OnSkipCardRelic` (Reina)
  * `OnRemoveCardFromMasterDeck` (Reina)

#### v1.7.0 ####
* `OnCardDrawPower`

#### v1.6.0 ####
* Keywords
  * Startup
* Add optional amount param to StunMonsterAction
* Make Temporary HP a multiword keyword
* Fix OnReceivePower to work with new ApplyPoisonOnRandomMonsterAction

#### v1.5.0 ####
* Enable Exhaustive and Refund variables (The_Evil_Pickle)
* Make Exhaustive and Refund automatic (The_Evil_Pickle)

#### v1.4.0 ####
* Keywords
  * Exhaustive (The_Evil_Pickle)
  * Refund (The_Evil_Pickle)
* Actions
  * `RefundAction` (The_Evil_Pickle)
  * `ModifyExhaustiveAction` (The_Evil_Pickle)
  * `EvokeSpecificOrbAction` (The_Evil_Pickle)
  * `TriggerPassiveAction` (The_Evil_Pickle)
* Power
  * `ExhaustiveNegationPower` (The_Evil_Pickle)

#### v1.3.0 ####
* `InvisiblePower`
* `OnAfterUseCardRelic`
* Fixes for week 46

#### v1.2.1 ####
* Fix for BaseMod's new max hand size changes

#### v1.2.0 ####
* `OnReceivePowerRelic`
* `OnReceivePowerPower`

#### v1.1.0 ####
* `ClickableRelic`
* `OnChannelRelic`
* `BetterOnLoseHpRelic`
* `BetterOnUsePotionRelic`
* `SuperRareRelic`

#### v1.0.1 ####
* Fix combat deck to master deck mapping

#### v1.0.0 ####
* Initial release
  * Actions
    * `StunMonsterAction`
    * `FetchAction`
    * `MoveCardsAction`
    * `AddTemporaryHPAction`
    * `RemoveAllTemporaryHPAction`
  * Keywords
    * Autoplay
    * Fleeting
    * Grave
    * Purge
    * Retain
    * Snecko
    * Soulbound
