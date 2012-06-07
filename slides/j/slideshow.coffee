# This Source Code Form is subject to the terms of the Mozilla Public
# License, v. 2.0. If a copy of the MPL was not distributed with this file,
# You can obtain one at http://mozilla.org/MPL/2.0/.

define ["jquery"
], ($) ->

  cycleSlides = (current) ->
    next = current.next(".slide")
    if next.length then next else current

  cycleSlidesBack = (current) ->
    next = current.prev(".slide")
    if next.length then next else current

  nextSlide = -> cycleSlides($(".current-slide").removeClass("current-slide")).addClass("current-slide")
  prevSlide = -> cycleSlidesBack($(".current-slide").removeClass("current-slide")).addClass("current-slide")

  $(".slide:first-child").addClass("current-slide")
  $(window).on "message", (e) ->
    msg = e.originalEvent?.data or e.data
    if msg == "nextSlide" then nextSlide()
    if msg == "previousSlide" then prevSlide()

  $(window).on "keydown", (e) ->
    if e.keyCode == 34 then nextSlide()
    if e.keyCode == 33 then prevSlide()

