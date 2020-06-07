Feature: Audiovisual management

    Scenario: Retrieve audiovisual title
        When I search audiovisual 1
        Then the audiovisual is found
        And his title is 'Breaking Bad'
