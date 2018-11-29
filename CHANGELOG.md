## Changelog ##
#### dev ####
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
