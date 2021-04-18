package scripts

import org.tribot.api.Timing
import org.tribot.api.input.Keyboard
import org.tribot.api2007.*
import org.tribot.api2007.Skills.SKILLS
import org.tribot.script.Script
import org.tribot.script.ScriptManifest
import org.tribot.script.interfaces.Painting
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import kotlin.random.Random

@ScriptManifest(authors = ["Powa", "IM4EVER12C"], category = "Fletching", name = "Powa's OSS Fletcher")
class Powagressive : Script(), Painting {
    private var shouldRun = true
    private val startTime = System.currentTimeMillis()
    private var fletchingLevelBefore = 0
    private var fletchingExpBefore = 0
    private var amountCut = 0
    private var logCounter = 0
    private val Bs = "Bow string"
    private val knife = "Knife"
    private var rand = (25..199).random().toLong()
    private var fLvl = Skills.getActualLevel(SKILLS.FLETCHING)

    enum class Bow(private val lvl: Int, val material: String, val product: String) {
        LOGS(1,  "Logs", "arrow shafts"),
        LONGBOW(10,  "Longbow (u)", "Longbow"),
        OAK_SHORT(20,  "Oak shortbow (u)", "Oak shortbow"),
        OAK_LONG(25,  "Oak longbow (u)", "Oak longbow"),
        WILLOW_SHORT(35, "Willow shortbow (u)", "Willow shortbow"),
        WILLOW_LONG(40, "Willow longbow (u)", "Willow longbow"),
        MAPLE_SHORT(50, "Maple shortbow (u)", "Maple shortbow"),
        MAPLE_LONG(55,  "Maple longbow (u)", "Maple longbow"),
        YEW_LONG(70, "Yew longbow (u)", "Yew longbow"),
        MAGIC_LONG(85, "Magic longbow (u)", "Magic longbow");

        private fun canString(): Boolean {
            return Skills.getActualLevel(SKILLS.FLETCHING) >= lvl
        }

        companion object {
            val targetBow: Bow
                get() {
                    var target = LOGS
                    for (b in Powagressive.Bow.values()) {
                        if (b.canString() && b.lvl > target.lvl) target = b
                    }
                    return target
                }
        }
    }

    override fun onPaint(g: Graphics) {
        val timeRan = System.currentTimeMillis() - startTime
        val alpha = 127
        val alpha2 = 186
        val myColour = Color(150, 150, 150, alpha)
        val myColour2 = Color(1, 1, 125, alpha2)
        g.color = myColour
        g.fillRect(5, 250, 510, 90)
        g.color = myColour2
        g.font = Font("Calibri", Font.BOLD, 24)
        g.drawString("Powa's OSS Fletcher", 20, 280)
        g.font = Font("Calibri", Font.BOLD, 16)
        g.color = Color.yellow
        g.drawString("| Bows Fletched: $amountCut", 280, 315)
        g.drawString("| Current Cut: " + Bow.targetBow.product, 10, 315)
        g.drawString("| Fletching Level: " + SKILLS.FLETCHING.actualLevel + " ("
                + (SKILLS.FLETCHING.actualLevel - fletchingLevelBefore) + ")", 10, 300)
        g.drawString("| Experience Gained: " + gainedXp(), 280, 300)
        g.drawString("| Time Elapsed: " + Timing.msToString(timeRan), 10, 330)
    }

    override fun run() {
        if (fletchingLevelBefore < 1) {
            fletchingLevelBefore = SKILLS.FLETCHING.actualLevel
        }
        while (shouldRun) {
            sleep(fletch())
        }
    }

    private fun getTotalFletched() {
        if (logCounter == 0) {
            logCounter = Inventory.getCount(Bow.targetBow.material)
        } else if (Inventory.getCount(Bow.targetBow.material) < logCounter) {
            amountCut += 1
            logCounter = Inventory.getCount(Bow.targetBow.material)
        }

    }

    private fun gainedLevel(): Int {
        var flvlCurrent = SKILLS.FLETCHING.actualLevel
        if (fLvl < flvlCurrent) {
            fLvl = flvlCurrent
        }
        return fLvl
    }

    private fun gainedXp(): Int {
        if (fletchingExpBefore < 1) {
            fletchingExpBefore = SKILLS.FLETCHING.xp
        }
        return SKILLS.FLETCHING.xp - fletchingExpBefore
    }

    private fun bankProcess() {
        if (Banking.isInBank()) {
            if (!Banking.isBankLoaded()) {
                Banking.openBank()
            }
            Banking.depositAll()
            if (gainedLevel() < 10) {
                if (Inventory.getCount(Bow.targetBow.material) < 1 && Banking.find(Bow.targetBow.material).isNotEmpty()) { Banking.withdraw(27, Bow.targetBow.material) }
                if (Inventory.getCount(knife) < 1 && Banking.find(knife).isNotEmpty()) { Banking.withdraw(1, knife) }
                else errorMessage()
            }
            if (gainedLevel() >= 10) {
                if (Inventory.getCount(Bow.targetBow.material) < 1 && Banking.find(Bow.targetBow.material).isNotEmpty()) { Banking.withdraw(14, Bow.targetBow.material) }
                if (Inventory.getCount(Bs) < 1 && Banking.find(Bs).isNotEmpty()) { Banking.withdraw(14, Bs) }
                else errorMessage()
            }
        } else errorMessage()
    }

    private fun whenCut() {
        when {
            gainedLevel() < 10 && Inventory.getCount(knife) >= 1 && Inventory.getCount(Bow.targetBow.material) >= 1 -> howCut()
            gainedLevel() >= 10 && Inventory.getCount(Bs) >= 1 && Inventory.getCount(Bow.targetBow.material) >= 1 -> howCut()
            else -> bankProcess()
        }
    }

    private fun howCut() {
        while (!Interfaces.isInterfaceSubstantiated(270)) {
            if (gainedLevel() < 10 && Inventory.find(knife).isNotEmpty() && Inventory.find(Bow.targetBow.material).isNotEmpty()) { Inventory.find(knife)[0].click() && Inventory.find(Bow.targetBow.material)[0].click() }
            if (gainedLevel() >= 10 && Inventory.find(Bs).isNotEmpty() && Inventory.find(Bow.targetBow.material).isNotEmpty()) { Inventory.find(Bs)[0].click() && Inventory.find(Bow.targetBow.material)[0].click() }
            sleep(1000,1600)
        }

        if (Timing.waitCondition({ Interfaces.isInterfaceSubstantiated(270) }, Random.nextLong(400, 600))) { Keyboard.sendPress(' ', 32) }

        while (Inventory.getCount(Bow.targetBow.material) > 0) {
            when {
                Inventory.getCount(Bow.targetBow.material) == 0 -> break
                gainedLevel() < 10 && Inventory.getCount(knife) == 0 -> break
                Inventory.getCount(Bs) == 0 -> break
                Interfaces.isInterfaceSubstantiated(233) -> break
                else -> sleep(rand)
            }
            sleep(1000,1600)
        }
    }

    private fun cutting() {
        if (Banking.isInBank()) {
            if (Banking.isBankLoaded()) { Banking.close() }
            whenCut()
        } else {
            errorMessage()
        }
    }

    private fun fletch(): Long {
        getTotalFletched()
        cutting()
        return rand
    }

    private fun errorMessage() {
        if (Login.getLoginState() == Login.STATE.INGAME) {
            shouldRun = if (!Banking.isInBank()) {
                println("Player not in Bank")
                false
            } else if (Inventory.getCount(Bow.targetBow.material) < 1) {
                println("No logs to fletch. Stopping script.")
                false
            } else if (Inventory.getCount(Powagressive().knife) < 1) {
                println("No knife found. Stopping script.")
                false
            } else if (Inventory.getCount(Powagressive().Bs) < 1) {
                println("No bow strings found. Stopping script.")
                false
            } else {
                println("Unknown Error")
                false
            }
        }
    }
}
