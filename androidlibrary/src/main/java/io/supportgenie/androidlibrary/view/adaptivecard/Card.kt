// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
package io.supportgenie.androidlibrary.view.adaptivecard

import java.io.IOException
import java.util.HashSet

import io.adaptivecards.objectmodel.AdaptiveCard
import io.adaptivecards.objectmodel.ParseContext
import io.adaptivecards.objectmodel.ParseResult
import io.adaptivecards.renderer.AdaptiveCardRenderer

/**
 * Created by almedina on 8/17/2018.
 */

class Card(fullCard: String) {

    var parsedCard: ParseResult? = null
        private set
    private var m_elementTypes: MutableSet<String>? = null
    var exceptionDetailMessage: String? = null
        private set

    init {
        fillElementTypes(fullCard)
    }

    private fun fillElementTypes(fullCardString: String) {
        m_elementTypes = HashSet()

        try {
            val context = ParseContext(null, null)
            parsedCard = AdaptiveCard.DeserializeFromString(fullCardString, AdaptiveCardRenderer.VERSION, context)
            /*
            if (parsedCard != null) {
                fillElementTypes(parsedCard!!.GetAdaptiveCard())
            }
            */
        } catch (e: IOException) {
            exceptionDetailMessage = e.message
        }
        /*
        not needed
        // Register all the types found into the bigger dictionary of existing element types
        for (elementType in m_elementTypes!!) {
            CardRetriever.getInstance().registerExistingCardElementType(elementType)
        }
         */
    }
    /*
    private fun fillElementTypes(adaptiveCard: AdaptiveCard?) {
        if (adaptiveCard == null) {
            return
        }

        fillElementTypes(adaptiveCard.GetBody())
        fillElementTypes(adaptiveCard.GetActions())
    }

    private fun fillElementTypes(elements: BaseCardElementVector?) {
        if (elements == null) {
            return
        }

        val elementsInCardCount = elements.size
        for (i in 0 until elementsInCardCount) {
            val element = elements[i]
            addElementToElementTypes(element.GetElementTypeString())
            fillElementTypes(element)
        }
    }

    private fun fillElementTypes(containerElement: BaseCardElement) {
        if (containerElement.GetElementType() == CardElementType.ColumnSet) {
            var columnSet: ColumnSet? = null
            if (containerElement is ColumnSet) {
                columnSet = containerElement
            } else if ((columnSet = ColumnSet.dynamic_cast(containerElement)) == null) {
                throw InternalError("Unable to convert BaseActionElement to ShowCardAction object model.")
            }

            fillElementTypes(columnSet)
        } else if (containerElement.GetElementType() == CardElementType.Container) {
            var container: Container? = null
            if (containerElement is Container) {
                container = containerElement
            } else if ((container = Container.dynamic_cast(containerElement)) == null) {
                throw InternalError("Unable to convert BaseActionElement to ShowCardAction object model.")
            }

            fillElementTypes(container)
        } else if (containerElement.GetElementType() == CardElementType.ImageSet) {
            var imageSet: ImageSet? = null
            if (containerElement is ImageSet) {
                imageSet = containerElement
            } else if ((imageSet = ImageSet.dynamic_cast(containerElement)) == null) {
                throw InternalError("Unable to convert BaseActionElement to ShowCardAction object model.")
            }

            fillElementTypes(imageSet)
        }
    }

    private fun fillElementTypes(container: Container?) {
        if (container == null) {
            return
        }

        fillElementTypes(container.GetItems())
    }

    private fun fillElementTypes(columnSet: ColumnSet?) {
        if (columnSet == null) {
            return
        }

        val columns = columnSet.GetColumns()
        val columnsCount = columns.size
        for (i in 0 until columnsCount) {
            val column = columns[i]
            addElementToElementTypes(column.GetElementTypeString())
            fillElementTypes(column.GetItems())
        }
    }

    private fun fillElementTypes(imageSet: ImageSet?) {
        if (imageSet == null) {
            return
        }

        val imageVector = imageSet.GetImages()
        if (!imageVector.isEmpty()) {
            addElementToElementTypes(imageVector[0].GetElementTypeString())
        }
    }

    private fun fillElementTypes(actions: BaseActionElementVector) {
        val actionsCount = actions.size
        for (i in 0 until actionsCount) {
            val action = actions[i]
            addElementToElementTypes(action.GetElementTypeString())

            if (action.GetElementType() == ActionType.ShowCard) {
                var showCardAction: ShowCardAction? = null
                if (action is ShowCardAction) {
                    showCardAction = action
                } else if ((showCardAction = ShowCardAction.dynamic_cast(action)) == null) {
                    throw InternalError("Unable to convert BaseActionElement to ShowCardAction object model.")
                }

                fillElementTypes(showCardAction!!.GetCard())
            }
        }
    }

    private fun addElementToElementTypes(elementType: String) {
        m_elementTypes!!.add(elementType.toLowerCase())
    }

    fun ContainsElementType(elementType: String): Boolean {
        return m_elementTypes!!.contains(elementType)
    }
    */
}
