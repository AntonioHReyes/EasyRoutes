package com.tonyakitori.inc.easyroutes.rest

import com.tonyakitori.inc.easyroutes.EasyRoutesDirections
import com.tonyakitori.inc.easyroutes.model.Directions

interface DirectionsRest {

    suspend fun getDirections(easyRoutesDirections: EasyRoutesDirections) : Directions

}