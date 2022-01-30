## Changelog ##
#### dev ####
* Automatically load localizations without needing to add new cases
* Localization
  * French (Diamsword)
* Add SpawnModificationCard interface (erasels)
  * Allows cards to customize how and if they will spawn
* Fix custom targeting to work with double play effects (Alchyr)
  * THIS IS A BREAKING CHANGE, any mods that have created a custom targe type will need to be fixed
* Custom icons for descriptions (Mistress Alison)
* Damage modifiers (Mistress Alison)
* Block modifiers (Mistress Alison)

#### v1.24.1 ####
* Fix crash on non-targeted cards selected with keyboard (Alchyr)

#### v1.24.0 ####
* Custom Targeting (Alchyr)
  * Self or Enemy targeting

#### v1.23.1 ####
* Fix `MoveCardsAction` always saying "Add to Hand"
  * Update localizations
    * ZHS
    * Korean
    * Russian
* Fix normal branching upgrades being random instead (Mistress Alison)

#### v1.23.0 ####
* Neutral Power type (erasels)

#### v1.22.0 ####
* Localization
  * Korean (Tsurumaki Maki + Celicath)
* Actions
  * `SelectCardsCenteredAction` (vex)
  * `DamageCallbackAction` (vex)

#### v1.21.3 ####
* Localization
  * Update Russian TempHP to remove underscore (Jedi)

#### v1.21.2 ####
* Fix `InvisiblePower` needing image
* Fix `InvisiblePower` notifying player when applied

#### v1.21.1 ####
* Fix keyword tooltips not rendering in SingleCardView (erasels)

#### v1.21.0 ####
* Powers
  * `BeforeRenderIntentPower` (erasels)
* Relics
  * `BeforeRenderIntentRelic` (erasels)

#### v1.20.4 ####
* Replace the Replace patch
* Fix `SelectCardsAction` for selecting multiple cards (rft50)

#### v1.20.3 ####
* Fix Writhing Mass intent changing when Stunned (Celicath)
* Remove block effect with TempHP (Celicath)
* Fix crash when hovering purge cards (erasels)

#### v1.20.2 ####
* Fix displaying common keyword icons on all cards in single card view

#### v1.20.1 ####
* Fix common keyword icons appearing on all tooltips for their keyword

#### v1.20.0 ####
* ZHS translation of branching upgrade UI
* Actions
  * `SelectCardsAction` (Jedi)
  * `SelectCardsInHandAction` (Jedi)
* Icons for common keywords (erasels/EatYourBeetS)
  * Exhaust
  * Ethereal
  * Purge
  * Innate
  * Retain

#### v1.19.3 ####
* Fix crash when using non-English

#### v1.19.2 ####
* Fix branching upgrades flashing in SingleCardView
* Add button to SingleCardView to preview alt upgrade

#### v1.19.1 ####
* Fix branching upgrades sometimes upgrading incorrectly (vex)

#### v1.19.0 ####
* Refactor branching upgrade cards
* Fix branching upgrades not working during upgrade events

#### v1.18.2 ####
* Fix branching upgrades not working when copying cards
* Fix infinite branch upgrades at campfires
* Allow card rewards to use branching upgrade some of the time

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
