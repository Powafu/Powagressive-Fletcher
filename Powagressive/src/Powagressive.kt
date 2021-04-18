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

    private var log = arrayOf(
            "Logs",
            "Longbow (u)",
            "Oak shortbow (u)",
            "Oak longbow (u)",
            "Willow shortbow (u)",
            "Willow longbow (u)",
            "Maple shortbow (u)",
            "Maple longbow (u)",
            "Yew longbow (u)",
            "Magic longbow (u)"
    )

    private var bowToFletch = arrayListOf(
            "arrow shafts",
            "Longbow",
            "Oak shortbow",
            "Oak longbow",
            "Willow shortbow",
            "Willow longbow",
            "Maple shortbow",
            "Maple longbow",
            "Yew longbow",
            "Magic longbow"
    )

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
        g.drawString("| Current Cut: " + bowType(), 10, 315)
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
            logCounter = Inventory.getCount(logType())
        } else if (Inventory.getCount(logType()) < logCounter) {
            amountCut += 1
            logCounter = Inventory.getCount(logType())
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

    private fun bowType(): String {
        return when (fLvl) {
            in 0..9 -> bowToFletch[0]
            in 10..19 -> bowToFletch[1]
            in 20..24 -> bowToFletch[2]
            in 25..34 -> bowToFletch[3]
            in 35..40 -> bowToFletch[4]
            in 40..49 -> bowToFletch[5]
            in 50..54 -> bowToFletch[6]
            in 55..69 -> bowToFletch[7]
            in 70..84 -> bowToFletch[8]
            in 85..99 -> bowToFletch[9]
            else -> "You're not in runescape anymore"
        }
    }

    private fun logType(): String {
        return when (fLvl) {
            in 0..9 -> log[0]
            in 10..19 -> log[1]
            in 20..24 -> log[2]
            in 25..34 -> log[3]
            in 35..40 -> log[4]
            in 40..49 -> log[5]
            in 50..54 -> log[6]
            in 55..69 -> log[7]
            in 70..84 -> log[8]
            in 85..99 -> log[9]
            else -> "Kansas"
        }
    }

    private fun bankProcess() {
        if (Banking.isInBank()) {
            if (!Banking.isBankLoaded()) {
                Banking.openBank()
            }
            Banking.depositAll()
            if (gainedLevel() < 10) {
                if (Inventory.getCount(logType()) < 1 && Banking.find(logType()).isNotEmpty()) { Banking.withdraw(27, logType()) }
                if (Inventory.getCount(knife) < 1 && Banking.find(knife).isNotEmpty()) { Banking.withdraw(1, knife) }
                else errorMessage()
            }
            if (gainedLevel() >= 10) {
                if (Inventory.getCount(logType()) < 1 && Banking.find(logType()).isNotEmpty()) { Banking.withdraw(14, logType()) }
                if (Inventory.getCount(Bs) < 1 && Banking.find(Bs).isNotEmpty()) { Banking.withdraw(14, Bs) }
                else errorMessage()
            }
        } else errorMessage()
    }

    private fun whenCut() {
        val myKnifeCount = Inventory.getCount(knife)
        val myLogTypeCount = Inventory.getCount(logType())
        val myBsCount = Inventory.getCount(Bs)

        when {
            gainedLevel() < 10 && myKnifeCount >= 1 && myLogTypeCount >= 1 -> howCut()
            gainedLevel() >= 10 && myBsCount >= 1 && myLogTypeCount >= 1 -> howCut()
            else -> bankProcess()
        }
    }

    private fun howCut() {
        val myKnife = Inventory.find(knife)
        val myBs = Inventory.find(Bs)
        val myLogType = Inventory.find(logType())

        while (!Interfaces.isInterfaceSubstantiated(270)) {
            if (gainedLevel() < 10 && myKnife.isNotEmpty() && myLogType.isNotEmpty()) { myKnife[0].click() && myLogType[0].click() }
            if (gainedLevel() >= 10 && myBs.isNotEmpty() && myLogType.isNotEmpty()) { myBs[0].click() && myLogType[0].click() }
            sleep(1000,1600)
        }

        if (Timing.waitCondition({ Interfaces.isInterfaceSubstantiated(270) }, Random.nextLong(400, 600))) { Keyboard.sendPress(' ', 32) }

        while (Inventory.getCount(logType()) > 0) {
            when {
                Inventory.getCount(logType()) == 0 -> break
                gainedLevel() < 10 && Inventory.getCount(knife) == 0 -> break
                Inventory.getCount(Bs) == 0 -> break
                !Inventory.open() -> break
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
            } else if (Inventory.getCount(Powagressive().logType()) < 1) {
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
