package com.janeullah.healthinspectionrecords.events

import com.janeullah.healthinspectionrecords.external.firebase.FirebaseInitialization
import com.janeullah.healthinspectionrecords.services.impl.HerokuBonsaiElasticSearchDocumentService
import com.janeullah.healthinspectionrecords.services.internal.RestaurantService
import spock.lang.Narrative
import spock.lang.Specification
import spock.lang.Title
import spock.lang.Unroll

@Title('Testing Orchestrator method')
@Narrative('''
I would like to be able to run a single command that orchestrates the execution of the following tasks:
1. Triggers downloads of the web pages if needed
2. Processes the web pages
3. Pushes the processed data to Firebase
4. Pushed the processed data to Heroku Bonsai (Elastic Search)
''')
// http://spockframework.org/spock/docs/1.1/interaction_based_testing.html
// https://code.google.com/archive/p/spock/wikis/Interactions.wiki
class ScheduledWebEventsSpec extends Specification {

    def firebaseInitialization = Mock(FirebaseInitialization)
    def webEventOrchestrator = Mock(WebEventOrchestrator)
    def restaurantService = Mock(RestaurantService)
    def herokuBonsaiElasticSearchDocumentService = Mock(HerokuBonsaiElasticSearchDocumentService)
    def scheduledWebEvents = new ScheduledWebEvents(
            firebaseInitialization,
            webEventOrchestrator,
            restaurantService,
            herokuBonsaiElasticSearchDocumentService)

    @Unroll("Evaluating runAllUpdates behavior restaurantCount #restaurantCount firebase #fbiCallStatus heroku #hbesCallStatus")
    "evaluating runAllUpdates"() {
        setup:
        restaurantService.getCount() >> restaurantCount
        fbiCount * firebaseInitialization.readRecordsFromLocalAndWriteToRemote() >> fbiCallStatus
        hbesCount * herokuBonsaiElasticSearchDocumentService.handleProcessingOfData() >> hbesCallStatus

        1 * restaurantService.deleteAllRecords()
        1 * webEventOrchestrator.processAndSaveAllRestaurants()

        when:
        boolean actualResult = scheduledWebEvents.runAllUpdates()

        then:
        actualResult == expectedResult

        where:
        restaurantCount | fbiCallStatus | hbesCallStatus | expectedResult | fbiCount | hbesCount
        10L             | true          | true           | true           | 1        | 1
        0L              | true          | true           | false          | 0        | 0
        1L              | false         | true           | false          | 1        | 0
        2L              | true          | false          | false          | 1        | 1
        3L              | false         | false          | false          | 1        | 0
    }

}