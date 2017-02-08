Nuetzliche Links
====
* Plugin-Examples: http://atlassian.bitbucket.org/#bitbucket
* SDK-Documentation: https://developer.atlassian.com/docs/developer-tools/working-with-the-sdk
* PR-UI-Tutorial: https://developer.atlassian.com/bitbucket/server/docs/latest/tutorials-and-examples/pull-request-overview.html
* Atlassian public repo: <https://maven.atlassian.com/public/>, <https://maven.atlassian.com/repository/public/> 
* Responding to application events: https://developer.atlassian.com/bitbucket/server/docs/latest/how-tos/responding-to-application-events.html
* [Bitbucket Java-API](https://developer.atlassian.com/bitbucket/server/docs/latest/reference/java-api.html)
* [Bitbucket Web-UI-API](https://developer.atlassian.com/bitbucket/server/docs/latest/reference/web-ui-api.html)
* [AUI Widgedlist](https://docs.atlassian.com/aui/latest/docs/inline-dialog.html)
* [AUI FAQ](https://docs.atlassian.com/aui/faq.html)
* [Velocity User Guide](http://velocity.apache.org/engine/1.6.2/user-guide.html)

Spring-Scanner
===============
Beispielprojekt: https://bitbucket.org/atlassian/atlassian-spring-scanner/src/3765f9fd87f28bb77b3ac9537421280294b0369b/atlassian-spring-scanner-test/?at=1.2.x
Dokumentation: https://bitbucket.org/atlassian/atlassian-spring-scanner/src/1.2.x/README.md?at=1.2.x&fileviewer=file-view-default

Hinweise:
=========
> Note that if your plugin targets versions before 4.7, you'll need to manually register and unregister your event listener. (https://developer.atlassian.com/bitbucket/server/docs/latest/how-tos/responding-to-application-events.html)



TODO
====
- Featureplan
    - Konfiguration
        - Benachrichtigung global einschalten? Benachr. nur f. diesen PR einschalten? Don't bother me again?
- Benachrichtigungen-Text (Inhalt) definieren
- Tests
    - Unit-Tests
    - Integrations-Tests: mehr darueber lernen
    - CI-System
- Marketplace
    - Plugin Deskriptor
    - Logo machen
    - Lizenz raus suchen
    - Lizensierung einbauen

Package-Regeln
==============
- Klassen duerfen nur andere Klassen auf der selben oder drueber liegenden Package-Ebene referenzieren
- Package namen sind im Singular
- Klassen duerfen beliebig Klassen in anderen Package-Pfaden referenzieren. Allerdings nicht ueber Kreuz: Klasse `a.b.A` darf `a.c.B` referenzieren, oder auch `a.c.d.C`. Dann darf allerdings `a.b.e.G` nicht `a.c.B` verwenden.
- Wenn ein Packagepfad von einem anderen Packagepfad verwendet wird `a.b.c` -> `a.b.d`, darf er nicht wieder zurueck verwendet werden: `a.b.d` -!-> `a.b.c`.
- ~~Klassennamen duerfen den letzten Package-Teil wiederholen, nicht mehr: `notification.slack.SlackFactory` ist ok. `notification.slack.NotificationSlackFactory` ist nicht ok.~~ Ist nicht haltbar. Innerhalb von `chatservice.slack` bietet sich nur `SlackChatService` an. Wobei. Einfach nur `ChatService` ist auch angebracht.

Features 0.1
=========
- Benachrichtigungs-Typen
    - pull
        - "Dein Pullrequest wurde approved/unapproved/declined <link>" (`PullRequestApprovedEvent, PullRequestUnapprovedEvent, PullRequestDeclinedEvent, PullRequestParticipantApprovedEvent, PullRequestParticipantReviewedEvent, PullRequestParticipantStatusUpdatedEvent, ApplicationPropertiesService.getBaseUrl()`)
- Versand an Slack
- Bitbucket-Username entspricht dem Slack-Usernamen
- Konfiguration
    - Global
        - Webhook URL mit erlaeuterungen

Features 0.2
============
- Konfiguration
    - Global
        - Test-Button (User-Namen oder Chat-Gruppen-Namen zum Testen eingeben)
        - System-Events an eine Chat-Gruppe einschalten
- Benachrichtigungen (global)
    - auth
        - AuthenticationFailureEvent - Event that is raised when an attempt to authenticate fails.
    - backup
        - BackupFailedEvent - Raised when a backup fails. This event is internally audited with a HIGH priority.
    - (g) MaintenanceStartedEvent - Fired when maintenance has begun.
    - (g) MaintenanceEndedEvent
    
Features 0.3
============
- Konfiguration
    - Per User
        - PR UI "Do you want to receive chat notification for pullrequest updates?". Wird angezeigt, wenn der User noch keine Konfigurationen hintelegt hate und es schon eine funktionierende glabale Konfiguration gibt.
            - wird eingeblendet sobald der User die PR Seite betritt 
            - Optionen: Yes, Not now, Don't bother me again
            - Yes: Folgedialog
                - Slack User-Name eingeben
                - Knopf um Test-Nachricht zu schicken
                - "Save" Knopf -> Benachrichtigung wo der User die Benachrichtigungen anpassen kann
            - Not now: fuer diesen PR nicht mehr fragen. Beim naechsten PR erneut fragen.
            - Don't bother me again: nie wieder fragen.
- Benachrichtigungen
    - PR mit dir als Reviewer erstellt
    - als Reviewer hinzugefuegt
    - Du bist als Reviewer entfernt worden
        - PullRequestParticipantsUpdatedEvent - An event raised when the total set of reviewers and role-less participants for a pull request changes.
        - PullRequestOpenedEvent
        - PullRequestRolesUpdatedEvent - An event raised when one or more users have had their explicit role in a pull request changed. Currently the only explicit role that can be changed is the REVIEWER role.
        - PullRequestParticipantsUpdatedEvent - An event raised when the total set of reviewers and role-less participants for a pull request changes.
    
Features 0.4
============
- Konfiguration
    - Per User
        - Konfigurations-Seite wo der User seine bisherigen Konfigurationen verwalten kann
            - Slack-Usernamen anpassen mit Test-Knopf
            - Events auswaehlen

Features 0.5
============
- [Tests](https://developer.atlassian.com/docs/getting-started/writing-and-running-plugin-tests)

Features 0.6
=============
- Benachrichtigungen
    - Dein Branch build war erflogreich (zuordnung des Commits zu Branch-Namen)
    - Dein PR-Build war erfolgreich (zuordnung des Commits zu PRs)
        - build (plugin)
            - BuildStatusSetEvent
    - Dein PR-Merge-Build war erfolgreich
    
Features 0.7
=============
- Benachrichtigungen
    - Kommentar
        - hinzugefuegt
        - bearbeitet
        - beantwortet
        - geloescht
    - PR gemerged (an reviewer)
    - Du bist als Reviewer hinzugefuegt worden
    - PR Beschreibung geaendert
    - PR enthaelt neue commits
- pull
    - PullRequestCommentActivityEvent - Event that is raised when a comment activity is created for a pull request.
    - PullRequestCommentAddedEvent - Event that is raised when a comment is added on a pull request. This does not include replies, which raise a PullRequestCommentRepliedEvent.
    - PullRequestCommentDeletedEvent
    - PullRequestCommentEditedEvent
    - PullRequestCommentRepliedEvent
    - PullRequestMergeActivityEvent - Event that is raised when a merge activity is created for a pull request.
    - PullRequestMergedEvent - Event raised when a pull request is merged via the web UI or REST, or when a remote merge is detected.
    - PullRequestReopenedEvent
    - PullRequestRescopeActivityEvent
    - PullRequestRescopedEvent - Event that is raised when the ref for the source-branch and/or the target-branch of a pull request is updated. Note that this not necessarily mean that the list of commits that the pull request covers will change.
    - PullRequestUpdatedEvent - Event that is raised when the pull request title or description are updated.


Future Features
==============

- Benachrichtigungs-Typen
    - PR
        - PR erstellt (bestimmtes Repository)
        - Dein PR/Your watched PR
            - declined/approved/needs work
            - Kommentar
            - mit Code-Vorschau
        - PR Build ist fehlgeschlagen/war erfolgreich
        - Commits hinzugefuegt
    - Branches
        - Dein Commit ist fehlgeschlagen/war erfolgreich (Build)
        - Commit in (d)einen Branch
        - Commitnachricht trifft auf einen Regex zu ("Version.*")
        - Jemand hat in Klassen comittet an denen du arbeitest
- Konfiguration
    - Global
        - Auswahl: Slack/Mattermost

- Customizations
    - Benachrichtigungs-Icon
    - Text-Farbe
    - Markierungs-Farbe
    - Nachricht-Template (selber coden)
    - Nachricht-Template auswaehlen (Minimalistisch, Aufuehrlich)
- Versand
    - Nur in Intervalen versenden (Focus Time)
    - Slack-Endpoint
    - Mattermost-Endpoint
- Diagnostics
    - Fehlerreporting
        - Unerwartete Exceptions sammeln
        - System User per Chat ueber neue Exceptions informieren
        - Fehler-Reporting per Mail
    - Logging
    - Ab und zu Konsistenzpruefung. (die PR, die ich noch zu dem User gespeichert habe, sind die ueberhaupt noch offen?)
- Repository Watcher: Ein Tag im Branch (master/release) wurde erstellt.
- Komplexe Benachrichtigungsmuster: beispiel: "PR erstellt" wenn Repo-/Branch-Name diesem Pattern entspricht, dann nachricht an diesen Webhook/Channel


Notizen
=======
- Persistierung
    - Kann Konfiguration pro User gespeichert werden? -> nein
    - Konfiguration-Persistierung rausfinden -> PluginSettingsFactory nur String, List und Map

Bitbucket-Events
================

    
User-Events
-----------
- commit
    - CommitDiscussionCommentAddedEvent - Event that is raised when a comment is added in a commit discussion. This does not include replies, which raise a CommitDiscussionCommentRepliedEvent.
    - CommitDiscussionCommentDeletedEvent
    - CommitDiscussionCommentEditedEvent - Event that is raised when a comment is edited in a commit discussion.
    - CommitDiscussionCommentRepliedEvent
    - CommitDiscussionParticipantsUpdatedEvent - An event raised when the participants in a commit discussion change. For example, when a user posts their first comment on a commit, or replies to an existing comment, they are added as a participant and this event is raised.
- maintenance
    - (g) MaintenanceStartedEvent - Fired when maintenance has begun.
    - (g) MaintenanceEndedEvent

- git (plugin)
    - GitPullRequestRefsChangedEvent (Event nach einem "rescope", wenn die tatsaechliche Aenderung durchgefuehrt wurde)
    - GitMergeRequestedEvent / GitPullRequestMergeRequestedEvent - Wenn der Merge tatsaechlich durchgefuehrt wurde

    

Slack Nachrichtenformat
=======================
* Links: `<https://api.slack.com|Slack API>`
* Nachrichten-Bild: `icon_url` Feld setzen
* Textformat
```
```pre```, `code`, _italic_, *bold*, ~strike~
```
* Nachrichten-Balken-Farbe: `color` Feld im Attachment

Mattermost/Slack Inkompatibilitaeten
====================================
(<https://docs.mattermost.com/developer/webhooks-incoming.html?highlight=webhook#known-slack-compatibility-issues>)
* Bold formatting as `*bold*` is not supported (must be done as `**bold**`)
* Webhooks cannot direct message the user who created the webhook. Man kann das zu seinem Vorteil nutzen. Mattermost hat per Default das Ueberschreiben des Absendernamens deaktiviert. So kann man sich einen User anlegen, der das Repository repraesentiert: "Bitbucket". Dann ist die Meldung gleich klarer.

