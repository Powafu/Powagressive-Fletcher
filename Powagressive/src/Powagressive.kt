package scripts

import org.tribot.api.Timing
import org.tribot.api2007.*
import org.tribot.api2007.Skills.SKILLS
import org.tribot.script.Script
import org.tribot.script.ScriptManifest
import org.tribot.script.interfaces.Painting
import java.awt.Color
import java.awt.Font
import java.awt.Graphics
import java.lang.Math.random

@ScriptManifest(authors = ["Powa", "IM4EVER12C"], category = "Fletching", name = "TestChange")
class Powagressive : Script(), Painting {
    private var shouldRun = true
    private val startTime = System.currentTimeMillis()
    private var fletchingLevelBefore = 0
    private var fletchingExpBefore = 0
    private var amountCut = 0
    private var logCounter = 0
    private val knife = "Knife"
    private val knifeA = "Knife ->"
    private var rand = (400..598).random().toLong()
    private var randLonger = (1000..2000).random().toLong()
    private val fLvl = Skills.getActualLevel(SKILLS.FLETCHING)
    private var log = arrayOf(
            "Logs",
            "Oak logs",
            "Willow logs",
            "Maple logs",
            "Yew logs",
            "Magic logs"
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
        g.drawString("Powagressive Open Source Fletcher", 20, 280)
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
        errorMessage()
    }

    private fun getTotalFletched() {
        if (logCounter == 0) {
            logCounter = Inventory.getCount(logType())
        } else if (Inventory.getCount(logType()) < logCounter) {
            amountCut += 1
            logCounter = Inventory.getCount(logType())
        }

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
            in 0..19 -> log[0]
            in 20..35 -> log[1]
            in 35..49 -> log[2]
            in 50..69 -> log[3]
            in 70..84 -> log[4]
            in 84..99 -> log[5]
            else -> "Kansas"
        }
    }

    private fun bankProcess() {
        if (Inventory.getCount(logType()) < 1) {
            if (!Banking.isBankLoaded()) {
                Banking.openBankBanker()
            } else {
                Banking.depositAllExcept(knife)
                if (Inventory.getCount(logType())< 1) {
                    Banking.withdraw(27, logType())
                    sleep(rand)

                }
                if (Inventory.getCount(knife) < 1) {
                    Banking.withdraw(1, knife)
                    Timing.waitCondition({(Inventory.getCount(knife) < 1)}, rand)


                }
            }
        }
        else if (!Banking.isBankLoaded()){
            Banking.close()
        }
    }

    private fun cutting() {
        if (Inventory.getCount(logType()) >= 1 && Inventory.getCount(knife) >= 1) {
            if (Banking.isBankLoaded()) {
                Banking.close()
            }
            val myKnife = Inventory.find(knife)
            val myLogType = Inventory.find(logType())
            if (Player.getAnimation() == -1) {
                if (knifeA !in Game.getUptext() && !Interfaces.isInterfaceValid(270)) {
                    myKnife[0].click("Use")
                }

                if (knifeA in Game.getUptext() && !Interfaces.isInterfaceValid(270)) {
                    val nxt : Int
                    val amt : Int = Inventory.getCount(logType())
                    nxt = (random() * (amt - 0)).toInt()
                    println(amt)
                    println(nxt)
                    myLogType[nxt].click()
                }
                Timing.waitCondition({ Interfaces.get(270) != null }, rand)

                if (Interfaces.isInterfaceSubstantiated(270, 16)) {
                    val rsInterfaceChildOption1 = Interfaces.get(270, 14)
                    val rsInterfaceChildOption2 = Interfaces.get(270, 15)
                    val rsInterfaceChildOption3 = Interfaces.get(270, 16)
                    val rsInterfaceChildOption4 = Interfaces.get(270, 17)
                    val rsInterfaceChildOption5 = Interfaces.get(270, 18)
                    when {
                        bowType() in rsInterfaceChildOption1.componentName -> rsInterfaceChildOption1.click()
                        bowType() in rsInterfaceChildOption2.componentName -> rsInterfaceChildOption2.click()
                        bowType() in rsInterfaceChildOption3.componentName -> rsInterfaceChildOption3.click()
                        bowType() in rsInterfaceChildOption4.componentName -> rsInterfaceChildOption4.click()
                        bowType() in rsInterfaceChildOption5.componentName -> rsInterfaceChildOption5.click()
                        else -> errorMessage()
                    }
                }
                sleep(1000)
            }
        }    else {
            bankProcess()
        }
    }


    private fun errorMessage() {
        shouldRun = when {
            Inventory.getCount(logType()) < 1 -> {
                println("No logs to fletch. Stopping script.")
                false
            }
            Inventory.getCount(knife) < 1 -> {
                println("No knife found. Stopping script.")
                false
            }
            else -> {
                println("Unknown Error")
                false
            }
        }
    }

    private fun fletch(): Long {
        getTotalFletched()
        cutting()
        return rand
    }
}
