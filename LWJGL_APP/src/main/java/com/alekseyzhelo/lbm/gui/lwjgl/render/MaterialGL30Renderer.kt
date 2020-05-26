package com.alekseyzhelo.lbm.gui.lwjgl.render

import com.alekseyzhelo.lbm.cli.CLISettings
import com.alekseyzhelo.lbm.core.cell.Material
import com.alekseyzhelo.lbm.core.cell.MaterialCellD2Q9
import com.alekseyzhelo.lbm.gui.lwjgl.cli.CMSettings
import com.alekseyzhelo.lbm.gui.lwjgl.color.FloatColor
import com.alekseyzhelo.lbm.util.normalize

class MaterialGL30Renderer(
    cli: CLISettings,
    cm: CMSettings,
    WIDTH: Int = 750,
    HEIGHT: Int = 750
) : GL30Renderer<MaterialCellD2Q9>(cli, cm, WIDTH, HEIGHT) {

    override fun cellColor(cell: MaterialCellD2Q9): FloatColor {
        return when (cell.material) {
            Material.NOTHING -> FloatColor(1.0f, 1.0f, 1.0f)
            Material.SOLID -> FloatColor(0.0f, 0.0f, 0.0f)
            else -> {
                colormap.getColor(normalize(cellValue(cell), minValue(), maxValue()).toFloat())
            }
        }
    }

}